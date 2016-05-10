package com.ee.imperator.data.db;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.sql.PreparedStatementBuilder;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.BatchGameProvider;
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.AttackedEntry;
import com.ee.imperator.game.log.CardsPlayedEntry;
import com.ee.imperator.game.log.ConqueredEntry;
import com.ee.imperator.game.log.EndedTurnEntry;
import com.ee.imperator.game.log.ForfeitedEntry;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Territory;
import com.ee.imperator.mission.Mission;
import com.ee.imperator.mission.PlayerMission;
import com.ee.imperator.user.Player;
import com.mysql.cj.api.jdbc.Statement;

public class SqlGameProvider implements BatchGameProvider {
	private static final Logger LOG = LogManager.createLogger();
	private static final String[] CARD_COLUMNS = new String[Card.values().length];
	static {
		CARD_COLUMNS[Card.ARTILLERY.ordinal()] = "c_art";
		CARD_COLUMNS[Card.INFANTRY.ordinal()] = "c_inf";
		CARD_COLUMNS[Card.CAVALRY.ordinal()] = "c_cav";
		CARD_COLUMNS[Card.JOKER.ordinal()] = "c_jok";
	}
	private final DataSource dataSource;

	public SqlGameProvider(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Game> getGames() {
		List<Game> games = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			loadGames(conn.prepareCall("SELECT `gid`, `map`, `name`, `uid`, `turn`, `time`, `state`, `units`, `conquered`, `password` FROM `games`").executeQuery(), games, conn);
		} catch (SQLException e) {
			LOG.e("Error loading games", e);
		}
		return games;
	}

	@Override
	public Game getGame(int id) {
		List<Game> games = new ArrayList<>(1);
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `gid`, `map`, `name`, `uid`, `turn`, `time`, `state`, `units`, `conquered`, `password` FROM `games` WHERE `gid` = ?");
			statement.setInt(1, id);
			loadGames(statement.executeQuery(), games, conn);
		} catch (SQLException e) {
			LOG.e("Error loading game " + id, e);
		}
		return games.isEmpty() ? null : games.get(0);
	}

	@Override
	public List<Integer> getGameIds() {
		List<Integer> ids = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			ResultSet result = conn.prepareCall("SELECT `gid` FROM `games`").executeQuery();
			while(result.next()) {
				ids.add(result.getInt(1));
			}
		} catch (SQLException e) {
			LOG.e("Error loading game ids", e);
		}
		return ids;
	}

	@Override
	public Collection<Game> getGames(Collection<Integer> ids) {
		List<Game> games = new ArrayList<>(ids.size());
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = new PreparedStatementBuilder("SELECT `gid`, `map`, `name`, `uid`, `turn`, `time`, `state`, `units`, `conquered`, `password` FROM `games` WHERE `gid` IN(").appendParameters(ids.size()).append(')').build(conn);
			int index = 1;
			for(int id : ids) {
				statement.setInt(index, id);
				index++;
			}
			loadGames(statement.executeQuery(), games, conn);
		} catch (SQLException e) {
			LOG.e("Error loading games by ids", e);
		}
		return games;
	}

	private void loadGames(ResultSet result, List<Game> games, Connection conn) throws SQLException {
		while(result.next()) {
			try {
				int id = result.getInt(1);
				com.ee.imperator.map.Map map = Imperator.getData().getMap(result.getInt(2)).clone();
				Map<Integer, Player> players = loadPlayers(id, map.getMissions());
				Game game = new Game(id, map, result.getString(3), result.getInt(4), result.getInt(5), result.getLong(6), Game.State.values()[result.getInt(7)], result.getInt(8), result.getBoolean(9), result.getString(10), players.values());
				loadTerritories(game, conn);
				loadAttacks(game, conn);
				games.add(game);
			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				LOG.e("Failed to load game", e);
			}
		}
		if(games.size() > 1) {
			games.sort(null);
		}
	}

	private Map<Integer, Player> loadPlayers(int id, Map<Integer, Mission> missions) throws SQLException {
		Map<Integer, Player> players = new HashMap<>();
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `uid`, `color`, `autoroll`, `mission`, `m_uid`, `state`, `c_art`, `c_cav`, `c_inf`, `c_jok` FROM `gamesjoined` WHERE `gid` = ?");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				Player player = new Player(Imperator.getData().getMember(result.getInt(1)));
				player.setColor(result.getString(2));
				player.setAutoRoll(result.getBoolean(3));
				Integer muid = result.getInt(5);
				if(result.wasNull()) {
					muid = null;
				}
				player.setMission(new PlayerMission(missions.get(result.getInt(4)), player, muid));
				player.setState(Player.State.values()[result.getInt(6)]);
				player.getCards().setArtillery(result.getInt(7));
				player.getCards().setCavalry(result.getInt(8));
				player.getCards().setInfantry(result.getInt(9));
				player.getCards().setJokers(result.getInt(10));
				players.put(player.getId(), player);
			}
		}
		return players;
	}

	private void loadTerritories(Game game, Connection conn) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("SELECT `territory`, `uid`, `units` FROM `territories` WHERE `gid` = ?");
		statement.setInt(1, game.getId());
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			Territory territory = game.getMap().getTerritories().get(result.getString(1));
			territory.setOwner(game.getPlayerById(result.getInt(2)));
			territory.setUnits(result.getInt(3));
		}
	}

	private void loadAttacks(Game game, Connection conn) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("SELECT `a_territory`, `d_territory`, `a_roll`, `transfer` FROM `attacks` WHERE `gid` = ?");
		statement.setInt(1, game.getId());
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			game.getAttacks().add(new Attack(game.getMap().getTerritories().get(result.getString(1)), game.getMap().getTerritories().get(result.getString(2)), result.getInt(4), getRoll(result.getString(3))));
		}
	}

	private static int[] getRoll(String rollString) {
		int[] roll = rollString != null ? new int[rollString.length()] : null;
		if(rollString != null) {
			for(int i = 0; i < roll.length; i++) {
				roll[i] = rollString.charAt(i) - '0';
			}
		}
		return roll;
	}

	@Override
	public void close() throws IOException {
		if(dataSource instanceof Closeable) {
			((Closeable) dataSource).close();
		} else if(dataSource instanceof AutoCloseable) {
			try {
				((AutoCloseable) dataSource).close();
			} catch (Exception e) {
				throw new IOException("Failed to close data source", e);
			}
		}
	}

	@Override
	public Game createGame(Player owner, com.ee.imperator.map.Map map, String name, String password) {
		try(Connection conn = dataSource.getConnection()) {
			StringBuilder query = new StringBuilder("INSERT INTO `games` (`map`, `name`, `uid`, `time`");
			if(password != null) {
				query.append(", `password`");
			}
			query.append(") VALUES(?, ?, ?, ?");
			if(password != null) {
				query.append(", ?");
			}
			query.append(')');
			PreparedStatement statement = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, map.getId());
			statement.setString(2, name);
			statement.setInt(3, owner.getId());
			long time = System.currentTimeMillis();
			statement.setLong(4, time);
			if(password != null) {
				statement.setString(5, password);
			}
			statement.execute();
			ResultSet result = statement.getGeneratedKeys();
			if(result.next()) {
				Game game = new Game(result.getInt(1), map, name, owner, password, time);
				addPlayerToGame(conn, owner, game);
				conn.commit();
				return game;
			}
		} catch(SQLException e) {
			LOG.e("Failed to create game", e);
		}
		return null;
	}

	private void addPlayerToGame(Connection conn, Player player, Game game) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("INSERT INTO `gamesjoined` (`gid`, `uid`, `color`) VALUES (?, ?, ?)");
		statement.setInt(1, game.getId());
		statement.setInt(2, player.getId());
		statement.setString(3, player.getColor());
		statement.execute();
	}

	@Override
	public boolean addPlayerToGame(Player player, Game game) {
		try(Connection conn = dataSource.getConnection()) {
			addPlayerToGame(conn, player, game);
			conn.commit();
			game.addPlayer(player);
			Imperator.getData().updateGameTime(game);
			return true;
		} catch (SQLException e) {
			LOG.e("Failed to add player to game", e);
		}
		return false;
	}

	@Override
	public boolean removePlayerFromGame(Player player, Game game) {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM `gamesjoined` WHERE `gid` = ? AND `uid` = ?");
			statement.setInt(1, game.getId());
			statement.setInt(2, player.getId());
			statement.execute();
			conn.commit();
			game.removePlayer(player);
			Imperator.getData().updateGameTime(game);
			return true;
		} catch (SQLException e) {
			LOG.e("Failed to remove player from game", e);
		}
		return false;
	}

	@Override
	public boolean deleteGame(Game game) {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM `chat` WHERE `gid` = ?");
			statement.setInt(1, game.getId());
			statement.execute();
			statement = conn.prepareStatement("DELETE FROM `games` WHERE `gid` = ?");
			statement.setInt(1, game.getId());
			statement.execute();
			conn.commit();
			return true;
		} catch (SQLException e) {
			LOG.e("Failed to delete game", e);
		}
		return false;
	}

	@Override
	public void startGame(Game game) {
		try(Connection conn = dataSource.getConnection()) {
			game.start();
			saveMissions(conn, game);
			saveTerritories(conn, game);
			updateTurn(conn, game);
			conn.commit();
		} catch (SQLException e) {
			LOG.e("Failed to start game", e);
		}
	}

	private void saveMissions(Connection conn, Game game) throws SQLException {
		for(Player player : game.getPlayers()) {
			PreparedStatement statement = conn.prepareStatement("UPDATE `gamesjoined` SET `mission` = ?, `m_uid` = ? WHERE `uid` = ? AND `gid` = ?");
			statement.setInt(1, player.getMission().getId());
			statement.setInt(2, player.getMission().getTargetId());
			statement.setInt(3, player.getId());
			statement.setInt(4, game.getId());
			statement.execute();
		}
	}

	private void saveTerritories(Connection conn, Game game) throws SQLException {
		StringBuilder query = new StringBuilder("INSERT INTO `territories` (`gid`, `territory`, `uid`, `units`) VALUES");
		for(int i = 0; i < game.getMap().getTerritories().size(); i++) {
			if(i > 0) {
				query.append(", ");
			}
			query.append("(?, ?, ?, ?)");
		}
		PreparedStatement statement = conn.prepareStatement(query.toString());
		int i = 1;
		for(Territory territory : game.getMap().getTerritories().values()) {
			statement.setInt(i++, game.getId());
			statement.setString(i++, territory.getId());
			statement.setInt(i++, territory.getOwner().getId());
			statement.setInt(i++, territory.getUnits());
		}
		statement.execute();
	}

	private void updateTurn(Connection conn, Game game) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("UPDATE `games` SET `time` = ?, `turn` = ? WHERE `gid` = ?");
		statement.setLong(1, game.getTime());
		statement.setInt(2, game.getCurrentPlayer() == null ? 0 : game.getCurrentPlayer().getId());
		statement.setInt(3, game.getId());
		statement.execute();
	}

	@Override
	public void updateGameTime(Game game) {
		game.setTime(System.currentTimeMillis());
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("UPDATE `games` SET `time` = ? WHERE `gid` = ?");
			statement.setLong(1, game.getTime());
			statement.setLong(2, game.getId());
			statement.execute();
			conn.commit();
		} catch (SQLException e) {
			LOG.e("Failed to update game time", e);
		}
	}

	@Override
	public List<LogEntry> getCombatLogs(Game game, long time) {
		List<LogEntry> entries = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `type`, `time`, `uid`, `num`, `char_three`, `d_roll`, `units`, `territory`, `d_territory` FROM `combatlog` WHERE `gid` = ? AND `time` > ? ORDER BY `time` ASC");
			statement.setInt(1, game.getId());
			statement.setLong(2, time);
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				LogEntry.Type type = LogEntry.Type.values()[result.getInt(1)];
				Player player = game.getPlayerById(result.getInt(3));
				time = result.getLong(2);
				LogEntry entry;
				if(type == LogEntry.Type.ATTACKED) {
					entry = new AttackedEntry(time, player,
							game.getPlayerById(result.getInt(4)),
							getRoll(result.getString(5)),
							getRoll(result.getString(6)),
							game.getMap().getTerritories().get(result.getString(8)),
							game.getMap().getTerritories().get(result.getString(9)));
				} else if(type == LogEntry.Type.CARDS_PLAYED) {
					entry = new CardsPlayedEntry(player, time, getRoll(result.getString(5)), result.getInt(7));
				} else if(type == LogEntry.Type.CONQUERED) {
					entry = new ConqueredEntry(player, time, game.getMap().getTerritories().get(result.getString(8)));
				} else if(type == LogEntry.Type.ENDED_TURN) {
					entry = new EndedTurnEntry(player, time);
				} else {
					entry = new ForfeitedEntry(player, time);
				}
				entries.add(entry);
			}
		} catch (SQLException e) {
			LOG.e("Failed to get combat log", e);
		}
		return entries;
	}

	@Override
	public void setAutoRoll(Player player, boolean autoroll) {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("UPDATE `gamesjoined` SET `autoroll` = ? WHERE `gid` = ? AND `uid` = ?");
			statement.setBoolean(1, autoroll);
			statement.setInt(2, player.getGame().getId());
			statement.setInt(3, player.getId());
			statement.execute();
			conn.commit();
			player.setAutoRoll(autoroll);
		} catch (SQLException e) {
			LOG.e("Failed to set autoroll", e);
		}
	}

	@Override
	public boolean addCards(Player player, Card card, int amount) {
		try(Connection conn = dataSource.getConnection()) {
			String col = CARD_COLUMNS[card.ordinal()];
			PreparedStatement statement = conn.prepareStatement("UPDATE `gamesjoined` SET `" + col + "` = `" + col + "` + ? WHERE `gid` = ? AND `uid` = ?");
			statement.setInt(1, amount);
			statement.setInt(2, player.getGame().getId());
			statement.setInt(3, player.getId());
			statement.execute();
			conn.commit();
			while(amount > 0) {
				player.getCards().add(card);
				amount--;
			}
			while(amount < 0) {
				player.getCards().remove(card);
				amount++;
			}
			return true;
		} catch (SQLException e) {
			LOG.e("Failed to add card", e);
		}
		return false;
	}

	@Override
	public void startTurn(Player player) {
		try(Connection conn = dataSource.getConnection()) {
			boolean conquered = false;
			Game.State state = Game.State.TURN_START;
			long time = System.currentTimeMillis();
			int units = player.getUnitsFromRegionsPerTurn();
			PreparedStatement statement = conn.prepareStatement("UPDATE `games` SET `state` = ?, `time` = ?, `units` = ?, `conquered` = ?, `turn` = ? WHERE `gid` = ?");
			statement.setInt(1, state.ordinal());
			statement.setLong(2, time);
			statement.setInt(3, units);
			statement.setBoolean(4, conquered);
			statement.setInt(5, player.getId());
			statement.setInt(6, player.getGame().getId());
			conn.commit();
			Game game = player.getGame();
			game.setTime(time);
			game.setCurrentTurn(player);
			game.setConquered(conquered);
			game.setState(state);
			game.setUnits(units);
		} catch (SQLException e) {
			LOG.e("Failed to start turn", e);
		}
	}
}
