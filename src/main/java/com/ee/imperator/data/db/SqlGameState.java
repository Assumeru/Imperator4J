package com.ee.imperator.data.db;

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
import com.ee.imperator.data.BatchGameState;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.data.transaction.sql.SqlGameTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Attack;
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
import com.ee.imperator.user.User;
import com.mysql.cj.api.jdbc.Statement;

public class SqlGameState extends CloseableDataSource implements BatchGameState {
	private static final Logger LOG = LogManager.createLogger();

	public SqlGameState(DataSource dataSource) {
		super(dataSource);
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
	public List<Game> getGames(User user) {
		List<Game> games = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `games`.`gid`, `games`.`map`, `games`.`name`, `games`.`uid`, `games`.`turn`, `games`.`time`, `games`.`state`, `games`.`units`, `games`.`conquered`, `games`.`password` FROM `games` JOIN `gamesjoined` ON(`games`.`gid` = `gamesjoined`.`gid`) WHERE `gamesjoined`.`gid` IN(SELECT `gid` FROM `gamesjoined` WHERE `uid` = ?) GROUP BY `gamesjoined`.`gid`");
			statement.setInt(1, user.getId());
			loadGames(statement.executeQuery(), games, conn);
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
	public Collection<Integer> getGameIds(User user) {
		List<Integer> ids = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `games`.`gid` FROM `games` JOIN `gamesjoined` ON(`games`.`gid` = `gamesjoined`.`gid`) WHERE `gamesjoined`.`gid` IN(SELECT `gid` FROM `gamesjoined` WHERE `uid` = ?) GROUP BY `gamesjoined`.`gid`");
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();
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
				com.ee.imperator.map.Map map = Imperator.getMapProvider().getMap(result.getInt(2)).copy();
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
				Player player = new Player(Imperator.getState().getMember(result.getInt(1)));
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
			game.getAttacks().add(new Attack(game.getMap().getTerritories().get(result.getString(1)), game.getMap().getTerritories().get(result.getString(2)), result.getInt(4), getInts(result.getString(3))));
		}
	}

	private static int[] getInts(String input) {
		int[] output = input != null ? new int[input.length()] : null;
		if(input != null) {
			for(int i = 0; i < output.length; i++) {
				output[i] = input.charAt(i) - '0';
			}
		}
		return output;
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
				Game game = new Game(result.getInt(1), map.copy(), name, owner, password, time);
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
							getInts(result.getString(5)),
							getInts(result.getString(6)),
							game.getMap().getTerritories().get(result.getString(8)),
							game.getMap().getTerritories().get(result.getString(9)));
				} else if(type == LogEntry.Type.CARDS_PLAYED) {
					entry = new CardsPlayedEntry(player, time, getInts(result.getString(5)), result.getInt(7));
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
	public GameTransaction modify(Game game) throws TransactionException {
		try {
			return new SqlGameTransaction(game, dataSource.getConnection());
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}
}
