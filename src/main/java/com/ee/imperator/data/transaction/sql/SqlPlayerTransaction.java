package com.ee.imperator.data.transaction.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.ee.sql.UpdateBuilder;

import com.ee.imperator.data.transaction.AbstractPlayerTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

public class SqlPlayerTransaction extends AbstractPlayerTransaction implements SqlChildTransaction {
	private final Connection connection;

	public SqlPlayerTransaction(Connection connection, Player player) {
		super(player);
		this.connection = connection;
	}

	@Override
	public void apply() {
		super.apply();
	}

	@Override
	public void commit() throws TransactionException {
		UpdateBuilder update = new UpdateBuilder("`gamesjoined`");
		setColumns(update);
		try {
			updateScore();
			if(update.getArguments() > 0) {
				PreparedStatement statement = update.toStatement(connection, "`uid` = ? AND `gid` = ?");
				statement.setInt(update.getArguments() + 1, player.getId());
				statement.setInt(update.getArguments() + 2, player.getGame().getId());
				statement.execute();
			}
		} catch (SQLException e) {
			throw new TransactionException("Failed to update player " + player.getId() + " in " + player.getGame().getId(), e);
		}
	}

	private void updateScore() throws SQLException {
		Member member = player.getMember();
		if(getScore() == Score.LOSS) {
			PreparedStatement statement;
			if(memberExists(member)) {
				statement = connection.prepareStatement("UPDATE `users` SET `score` = `score` - 1, `losses` = `losses` + 1 WHERE `uid` = ?");
			} else {
				statement = connection.prepareStatement("INSERT INTO `users` (`score`, `losses`) VALUES(-1, 1) WHERE `uid` = ?");
			}
			statement.setInt(1, member.getId());
			statement.execute();
		} else if(getScore() == Score.WIN) {
			PreparedStatement statement;
			if(memberExists(member)) {
				statement = connection.prepareStatement("UPDATE `users` SET `score` = `score` + ?, `wins` = `wins` + 1 WHERE `uid` = ?");
			} else {
				statement = connection.prepareStatement("INSERT INTO `users` (`score`, `wins`) VALUES(?, 1) WHERE `uid` = ?");
			}
			statement.setInt(1, player.getGame().getPlayers().size() - 1);
			statement.setInt(2, member.getId());
			statement.execute();
		}
	}

	private boolean memberExists(Member member) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM `users` WHERE `uid` = ?");
		statement.setInt(1, member.getId());
		return statement.executeQuery().next();
	}

	private void setColumns(UpdateBuilder update) {
		if(player.getAutoRoll() != getAutoroll()) {
			update.setColumn("`autoroll`", PreparedStatement::setBoolean, getAutoroll());
		}
		if(player.getMission() != getMission()) {
			update.setColumn("`mission`", PreparedStatement::setInt, getMission().getId());
			update.setColumn("`m_uid`", PreparedStatement::setInt, getMission().getTargetId());
		}
		if(player.getState() != getState()) {
			update.setColumn("`state`", PreparedStatement::setInt, getState().ordinal());
		}
		if(player.getCards().getArtillery() != getCards().getArtillery()) {
			update.setColumn("`c_art`", PreparedStatement::setInt, getCards().getArtillery());
		}
		if(player.getCards().getCavalry() != getCards().getCavalry()) {
			update.setColumn("`c_cav`", PreparedStatement::setInt, getCards().getCavalry());
		}
		if(player.getCards().getInfantry() != getCards().getInfantry()) {
			update.setColumn("`c_int`", PreparedStatement::setInt, getCards().getInfantry());
		}
		if(player.getCards().getJokers() != getCards().getJokers()) {
			update.setColumn("`c_jok`", PreparedStatement::setInt, getCards().getJokers());
		}
	}
}
