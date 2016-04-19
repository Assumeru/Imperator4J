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
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;
import com.ee.imperator.mission.Mission;
import com.ee.imperator.mission.PlayerMission;
import com.ee.imperator.user.Player;

public class SqlGameProvider implements BatchGameProvider {
	private static final Logger LOG = LogManager.createLogger();
	private final DataSource dataSource;

	public SqlGameProvider(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Game> getGames() {
		List<Game> games = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			loadGames(conn.prepareCall("SELECT gid, map, name, uid, turn, time, state, units, conquered, password FROM games").executeQuery(), games);
		} catch (SQLException e) {
			LOG.e("Error loading games", e);
		}
		return games;
	}

	@Override
	public Game getGame(int id) {
		List<Game> games = new ArrayList<>(1);
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT gid, map, name, uid, turn, time, state, units, conquered, password FROM games WHERE gid = ?");
			statement.setInt(1, id);
			loadGames(statement.executeQuery(), games);
		} catch (SQLException e) {
			LOG.e("Error loading game " + id, e);
		}
		return games.isEmpty() ? null : games.get(0);
	}

	@Override
	public List<Integer> getGameIds() {
		List<Integer> ids = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			ResultSet result = conn.prepareCall("SELECT gid FROM games").executeQuery();
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
			PreparedStatement statement = new PreparedStatementBuilder("SELECT gid, map, name, uid, turn, time, state, units, conquered, password FROM games WHERE gid IN(").appendParameters(ids.size()).append(')').build(conn);
			int index = 1;
			for(int id : ids) {
				statement.setInt(index, id);
				index++;
			}
			loadGames(statement.executeQuery(), games);
		} catch (SQLException e) {
			LOG.e("Error loading games by ids", e);
		}
		return games;
	}

	private void loadGames(ResultSet result, List<Game> games) throws SQLException {
		while(result.next()) {
			try {
				int id = result.getInt(1);
				com.ee.imperator.map.Map map = Imperator.getData().getMap(result.getInt(2)).clone();
				Map<Integer, Player> players = loadPlayers(id, map.getMissions());
				Game game = new Game(id, map, result.getString(3), result.getInt(4), result.getInt(5), result.getLong(6), Game.State.values()[result.getInt(7)], result.getInt(8), result.getBoolean(9), result.getString(10), players.values());
				loadTerritories(game);
				games.add(game);
			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				LOG.e("Failed to load game", e);
			}
		}
		games.sort(null);
	}

	private Map<Integer, Player> loadPlayers(int id, Map<Integer, Mission> missions) throws SQLException {
		Map<Integer, Player> players = new HashMap<>();
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT uid, color, autoroll, mission, m_uid, state, c_art, c_cav, c_inf, c_jok WHERE gid = ?");
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

	private void loadTerritories(Game game) throws SQLException {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT territory, uid, units FROM territories WHERE gid = ?");
			statement.setInt(1, game.getId());
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				Territory territory = game.getMap().getTerritories().get(result.getString(1));
				territory.setOwner(game.getPlayerById(result.getInt(2)));
				territory.setUnits(result.getInt(3));
			}
		}
	}

	@Override
	public void close() throws IOException {
		if(dataSource instanceof Closeable) {
			((Closeable) dataSource).close();
		} else if(dataSource instanceof AutoCloseable) {
			try {
				((AutoCloseable) dataSource).close();
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}
}