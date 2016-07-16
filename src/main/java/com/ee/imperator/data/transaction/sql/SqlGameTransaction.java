package com.ee.imperator.data.transaction.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.ee.sql.UpdateBuilder;

import com.ee.imperator.data.transaction.AbstractGameTransaction;
import com.ee.imperator.data.transaction.PlayerTransaction;
import com.ee.imperator.data.transaction.TerritoryTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.AttackedEntry;
import com.ee.imperator.game.log.CardsPlayedEntry;
import com.ee.imperator.game.log.ConqueredEntry;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public class SqlGameTransaction extends AbstractGameTransaction<SqlChildTransaction> {
	private final Connection connection;
	private TerritoryInsertQuery insertQuery;

	public SqlGameTransaction(Game game, Connection connection) {
		super(game);
		this.connection = connection;
	}

	private static String toString(int[] roll) {
		StringBuilder sb = new StringBuilder();
		for(int d : roll) {
			sb.append(String.valueOf(d));
		}
		return sb.toString();
	}

	@Override
	public void commit() throws TransactionException {
		UpdateBuilder update = new UpdateBuilder("`games`");
		setColumns(update);
		try {
			boolean shouldCommit = removeTerritories();
			shouldCommit |= removeLogEntries();
			shouldCommit |= insertLogEntries();
			shouldCommit |= removeAttacks();
			shouldCommit |= insertAttacks();
			shouldCommit |= removePlayers();
			shouldCommit |= insertPlayers();
			if(update.getArguments() > 0) {
				PreparedStatement statement = update.toStatement(connection, "`gid` = ?");
				statement.setInt(update.getArguments() + 1, game.getId());
				statement.execute();
				commitChildren();
				super.commit();
			} else if(!children.isEmpty()) {
				commitChildren();
			} else if(shouldCommit) {
				connection.commit();
			}
		} catch (SQLException e) {
			throw new TransactionException("Failed to update game " + game.getId(), e);
		}
	}

	private boolean removeTerritories() throws SQLException {
		if(willDeleteTerritories()) {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM `territories` WHERE `gid` = ?");
			statement.setInt(1, game.getId());
			statement.execute();
		}
		return willDeleteTerritories();
	}

	private boolean removeLogEntries() throws SQLException {
		if(willDeleteLogEntries()) {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM `combatlog` WHERE `gid` = ?");
			statement.setInt(1, game.getId());
			statement.execute();
		}
		return willDeleteLogEntries();
	}

	private boolean insertLogEntries() throws SQLException {
		if(!getLogEntries().isEmpty()) {
			for(LogEntry entry : getLogEntries()) {
				insertLogEntry(entry);
			}
			return true;
		}
		return false;
	}

	private void insertLogEntry(LogEntry entry) throws SQLException {
		PreparedStatement statement;
		if(entry.getType() == LogEntry.Type.ATTACKED) {
			statement = connection.prepareStatement("INSERT INTO `combatlog` (`gid`, `uid`, `type`, `time`, `num`, `char_three`, `d_roll`, `territory`, `d_territory`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
		} else if(entry.getType() == LogEntry.Type.CONQUERED) {
			statement = connection.prepareStatement("INSERT INTO `combatlog` (`gid`, `uid`, `type`, `time`, `territory`) VALUES(?, ?, ?, ?, ?)");
		} else if(entry.getType() == LogEntry.Type.CARDS_PLAYED) {
			statement = connection.prepareStatement("INSERT INTO `combatlog` (`gid`, `uid`, `type`, `time`, `char_three`, `units`) VALUES(?, ?, ?, ?, ?, ?)");
		} else {
			statement = connection.prepareStatement("INSERT INTO `combatlog` (`gid`, `uid`, `type`, `time`) VALUES(?, ?, ?, ?)");
		}
		statement.setInt(1, entry.getPlayer().getGame().getId());
		statement.setInt(2, entry.getPlayer().getId());
		statement.setInt(3, entry.getType().ordinal());
		statement.setLong(4, entry.getTime());
		if(entry instanceof AttackedEntry) {
			AttackedEntry attack = (AttackedEntry) entry;
			statement.setInt(5, attack.getDefender().getId());
			statement.setString(6, toString(attack.getAttackRoll()));
			statement.setString(7, toString(attack.getDefendRoll()));
			statement.setString(8, attack.getAttacking().getId());
			statement.setString(9, attack.getDefending().getId());
		} else if(entry instanceof ConqueredEntry) {
			statement.setString(5, ((ConqueredEntry) entry).getTerritory().getId());
		} else if(entry instanceof CardsPlayedEntry) {
			CardsPlayedEntry cards = (CardsPlayedEntry) entry;
			statement.setString(5, toString(cards.getCards()));
			statement.setInt(6, cards.getUnits());
		}
		statement.execute();
	}

	private boolean removeAttacks() throws SQLException {
		if(!getRemovedAttacks().isEmpty()) {
			for(Attack attack : getRemovedAttacks()) {
				PreparedStatement statement = connection.prepareStatement("DELETE FROM `attacks` WHERE `gid` = ? AND `a_territory` = ? AND `d_territory` = ?");
				statement.setInt(1, game.getId());
				statement.setString(2, attack.getAttacker().getId());
				statement.setString(3, attack.getDefender().getId());
				statement.execute();
			}
			return true;
		}
		return false;
	}

	private boolean insertAttacks() throws SQLException {
		if(!getAddedAttacks().isEmpty()) {
			for(Attack attack : getAddedAttacks()) {
				PreparedStatement statement = connection.prepareStatement("INSERT INTO `attacks` (`gid`, `a_territory`, `d_territory`, `a_roll`, `transfer`) VALUES(?, ?, ?, ?, ?)");
				statement.setInt(1, game.getId());
				statement.setString(2, attack.getAttacker().getId());
				statement.setString(3, attack.getDefender().getId());
				statement.setString(4, toString(attack.getAttackRoll()));
				statement.setInt(5, attack.getMove());
				statement.execute();
			}
			return true;
		}
		return false;
	}

	private boolean removePlayers() throws SQLException {
		if(!getRemovedPlayers().isEmpty()) {
			for(Player player : getRemovedPlayers()) {
				PreparedStatement statement = connection.prepareStatement("DELETE FROM `gamesjoined` WHERE `gid` = ? AND `uid` = ?");
				statement.setInt(1, game.getId());
				statement.setInt(2, player.getId());
				statement.execute();
			}
			return true;
		}
		return false;
	}

	private boolean insertPlayers() throws SQLException {
		if(!getAddedPlayers().isEmpty()) {
			for(Player player : getAddedPlayers()) {
				PreparedStatement statement = connection.prepareStatement("INSERT INTO `gamesjoined` (`gid`, `uid`, `color`) VALUES (?, ?, ?)");
				statement.setInt(1, game.getId());
				statement.setInt(2, player.getId());
				statement.setString(3, player.getColor());
				statement.execute();
			}
			return true;
		}
		return false;
	}

	private void commitChildren() throws TransactionException, SQLException {
		for(SqlChildTransaction child : children) {
			child.commit();
		}
		connection.commit();
		for(SqlChildTransaction child : children) {
			child.apply();
		}
	}

	private void setColumns(UpdateBuilder update) {
		if(game.getCurrentPlayer() != getCurrentTurn()) {
			update.setColumn("`turn`", PreparedStatement::setInt, getCurrentTurn() == null ? 0 : getCurrentTurn().getId());
		}
		if(game.getState() != getState()) {
			update.setColumn("`state`", PreparedStatement::setInt, getState().ordinal());
		}
		if(game.getTime() != getTime()) {
			update.setColumn("`time`", PreparedStatement::setLong, getTime());
		}
		if(game.getUnits() != getUnits()) {
			update.setColumn("`units`", PreparedStatement::setInt, getUnits());
		}
		if(game.hasConquered() != hasConquered()) {
			update.setColumn("`conquered`", PreparedStatement::setBoolean, hasConquered());
		}
	}

	@Override
	public void close() throws TransactionException {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new TransactionException("Failed to close transaction", e);
		}
	}

	@Override
	protected PlayerTransaction getTransaction(Player player) {
		return new SqlPlayerTransaction(connection, player);
	}

	@Override
	protected TerritoryTransaction getTransaction(Territory territory) {
		if(territory.getOwner() == null) {
			if(insertQuery == null) {
				insertQuery = new TerritoryInsertQuery(connection, game);
			}
			return new SqlInsertTerritoryTransaction(territory, insertQuery);
		}
		return new SqlTerritoryTransaction(connection, game, territory);
	}
}
