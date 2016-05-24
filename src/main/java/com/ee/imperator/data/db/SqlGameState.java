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
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Cards;
import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.Game.State;
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
	private static final String[] CARD_COLUMNS = new String[Card.values().length];
	static {
		CARD_COLUMNS[Card.ARTILLERY.ordinal()] = "c_art";
		CARD_COLUMNS[Card.INFANTRY.ordinal()] = "c_inf";
		CARD_COLUMNS[Card.CAVALRY.ordinal()] = "c_cav";
		CARD_COLUMNS[Card.JOKER.ordinal()] = "c_jok";
	}

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
				com.ee.imperator.map.Map map = Imperator.getMapProvider().getMap(result.getInt(2)).clone();
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
				Game game = new Game(result.getInt(1), map.clone(), name, owner, password, time);
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
			long time = System.currentTimeMillis();
			addPlayerToGame(conn, player, game);
			updateGameTime(conn, game, time);
			conn.commit();
			game.setTime(time);
			game.addPlayer(player);
			return true;
		} catch (SQLException e) {
			LOG.e("Failed to add player to game", e);
		}
		return false;
	}

	@Override
	public boolean removePlayerFromGame(Player player, Game game) {
		try(Connection conn = dataSource.getConnection()) {
			long time = System.currentTimeMillis();
			PreparedStatement statement = conn.prepareStatement("DELETE FROM `gamesjoined` WHERE `gid` = ? AND `uid` = ?");
			statement.setInt(1, game.getId());
			statement.setInt(2, player.getId());
			statement.execute();
			updateGameTime(conn, game, time);
			conn.commit();
			game.removePlayer(player);
			game.setTime(time);
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

	private void updateGameTime(Connection conn, Game game, long time) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("UPDATE `games` SET `time` = ? WHERE `gid` = ?");
		statement.setLong(1, time);
		statement.setLong(2, game.getId());
		statement.execute();
		conn.commit();
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
			statement.execute();
			saveLogEntry(conn, new EndedTurnEntry(player.getGame().getCurrentPlayer(), time));
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

	private void saveLogEntry(Connection conn, LogEntry entry) throws SQLException {
		PreparedStatement statement;
		if(entry.getType() == LogEntry.Type.ATTACKED) {
			statement = conn.prepareStatement("INSERT INTO `combatlog` (`gid`, `uid`, `type`, `time`, `num`, `char_three`, `d_roll`, `territory`, `d_territory`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
		} else if(entry.getType() == LogEntry.Type.CONQUERED) {
			statement = conn.prepareStatement("INSERT INTO `combatlog` (`gid`, `uid`, `type`, `time`, `territory`) VALUES(?, ?, ?, ?, ?)");
		} else if(entry.getType() == LogEntry.Type.CARDS_PLAYED) {
			statement = conn.prepareStatement("INSERT INTO `combatlog` (`gid`, `uid`, `type`, `time`, `char_three`, `units`) VALUES(?, ?, ?, ?, ?, ?)");
		} else {
			statement = conn.prepareStatement("INSERT INTO `combatlog` (`gid`, `uid`, `type`, `time`) VALUES(?, ?, ?, ?)");
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
			statement.setString(3, toString(cards.getCards()));
			statement.setInt(4, cards.getUnits());
		}
		statement.execute();
	}

	@Override
	public void updateUnitsAndState(Game game, State state, int units) {
		try(Connection conn = dataSource.getConnection()) {
			long time = System.currentTimeMillis();
			PreparedStatement statement = conn.prepareStatement("UPDATE `games` SET `units` = `units` + ?, `state` = ?, `time` = ? WHERE `gid` = ?");
			statement.setInt(1, units);
			statement.setInt(2, state.ordinal());
			statement.setLong(3, time);
			statement.setInt(4, game.getId());
			statement.execute();
			conn.commit();
			game.setUnits(game.getUnits() + units);
			game.setState(state);
			game.setTime(time);
		} catch (SQLException e) {
			LOG.e("Failed to update state and units", e);
		}
	}

	@Override
	public void placeUnits(Game game, Territory territory, int units) {
		try(Connection conn = dataSource.getConnection()) {
			long time = System.currentTimeMillis();
			PreparedStatement statement = conn.prepareStatement("UPDATE `games` SET `units` = `units` - ?, `time` = ? WHERE `gid` = ?");
			statement.setInt(1, units);
			statement.setLong(2, time);
			statement.setInt(3, game.getId());
			statement.execute();
			statement = conn.prepareStatement("UPDATE `territories` SET `units` = `units` + ? WHERE `gid` = ? AND `territory` = ?");
			statement.setInt(1, units);
			statement.setInt(2, game.getId());
			statement.setString(3, territory.getId());
			statement.execute();
			conn.commit();
			game.setUnits(game.getUnits() - units);
			game.setTime(time);
			territory.setUnits(territory.getUnits() + units);
		} catch (SQLException e) {
			LOG.e("Failed to place units", e);
		}
	}

	@Override
	public void forfeit(Player player) {
		try(Connection conn = dataSource.getConnection()) {
			Player.State state = Player.State.GAME_OVER;
			PreparedStatement statement = conn.prepareStatement("UPDATES `gamesjoined` SET `autoroll` = ?, `state` = ? WHERE `gid` = ? AND `uid` = ?");
			statement.setBoolean(1, true);
			statement.setInt(2, state.ordinal());
			statement.setInt(3, player.getGame().getId());
			statement.setInt(4, player.getId());
			statement.execute();
			saveLogEntry(conn, new ForfeitedEntry(player, System.currentTimeMillis()));
			conn.commit();
			player.setState(state);
			player.setAutoRoll(true);
		} catch (SQLException e) {
			LOG.e("Failed to surrender", e);
		}
	}

	private static String toString(int[] roll) {
		StringBuilder sb = new StringBuilder();
		for(int d : roll) {
			sb.append(String.valueOf(d));
		}
		return sb.toString();
	}

	@Override
	public void setState(Game game, State state) {
		try(Connection conn = dataSource.getConnection()) {
			long time = System.currentTimeMillis();
			PreparedStatement statement = conn.prepareStatement("UPDATE `games` SET `state` = ?, `time` = ? WHERE `gid` = ?");
			statement.setInt(1, state.ordinal());
			statement.setLong(2, time);
			statement.setInt(3, game.getId());
			statement.execute();
			conn.commit();
			game.setState(state);
			game.setTime(time);
		} catch (SQLException e) {
			LOG.e("Failed to set state", e);
		}
	}

	@Override
	public void saveAttack(Game game, Attack attack) {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("INSERT INTO `attacks` (`gid`, `a_territory`, `d_territory`, `a_roll`, `transfer`) VALUES(?, ?, ?, ?, ?)");
			statement.setInt(1, game.getId());
			statement.setString(2, attack.getAttacker().getId());
			statement.setString(3, attack.getDefender().getId());
			statement.setString(4, toString(attack.getAttackRoll()));
			statement.setInt(5, attack.getMove());
			statement.execute();
			conn.commit();
			game.getAttacks().add(attack);
		} catch (SQLException e) {
			LOG.e("Failed to attack", e);
		}
	}

	@Override
	public void attack(Game game, Attack attack) {
		try(Connection conn = dataSource.getConnection()) {
			long time = System.currentTimeMillis();
			saveLogEntry(conn, new AttackedEntry(time, attack.getAttacker().getOwner(), attack.getDefender().getOwner(), attack.getAttackRoll(), attack.getDefendRoll(), attack.getAttacker(), attack.getDefender()));
			int attackerUnits = attack.getAttacker().getUnits() - attack.getAttackLosses();
			int defenderUnits = attack.getDefender().getUnits() - attack.getDefendLosses();
			Player dOwner = attack.getDefender().getOwner();
			PreparedStatement statement;
			boolean conquered = defenderUnits < 1;
			if(conquered) {
				saveLogEntry(conn, new ConqueredEntry(attack.getAttacker().getOwner(), time, attack.getDefender()));
				dOwner = attack.getAttacker().getOwner();
				int move = attack.getMove();
				if(move >= attackerUnits) {
					move = attackerUnits - 1;
				}
				attackerUnits -= move;
				defenderUnits = move;
				statement = conn.prepareStatement("UPDATE `games` SET `conquered` = ?, `time` = ? WHERE `gid` = ?");
				statement.setBoolean(1, conquered);
				statement.setLong(2, time);
				statement.setInt(3, game.getId());
				statement.execute();
			}
			if(attackerUnits != attack.getAttacker().getUnits()) {
				statement = conn.prepareStatement("UPDATE `territories` SET `units` = ? WHERE `territory` = ? AND `gid` = ?");
				statement.setInt(1, attackerUnits);
				statement.setString(2, attack.getAttacker().getId());
				statement.setInt(3, game.getId());
				statement.execute();
			}
			if(defenderUnits != attack.getDefender().getUnits() || dOwner != attack.getDefender().getOwner()) {
				statement = conn.prepareStatement("UPDATE `territories` SET `units` = ?, `uid` = ? WHERE `territory` = ? AND `gid` = ?");
				statement.setInt(1, defenderUnits);
				statement.setInt(2, dOwner.getId());
				statement.setString(3, attack.getDefender().getId());
				statement.setInt(4, game.getId());
				statement.execute();
			}
			conn.commit();
			attack.getAttacker().setUnits(attackerUnits);
			attack.getDefender().setUnits(defenderUnits);
			attack.getDefender().setOwner(dOwner);
			game.setConquered(game.hasConquered() || conquered);
			game.setTime(time);
		} catch (SQLException e) {
			LOG.e("Failed to attack", e);
		}
	}

	@Override
	public void setState(Player player, Player.State state) {
		try(Connection conn = dataSource.getConnection()) {
			setState(conn, player, state);
			conn.commit();
			player.setState(state);
		} catch (SQLException e) {
			LOG.e("Failed to save player state", e);
		}
	}

	private void setState(Connection conn, Player player, Player.State state) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("UPDATE `gamesjoined` SET `state` = ? WHERE `gid` = ? AND `uid` = ?");
		statement.setInt(1, state.ordinal());
		statement.setInt(2, player.getGame().getId());
		statement.setInt(3, player.getId());
		statement.execute();
	}

	@Override
	public void saveMissions(Game game) {
		try(Connection conn = dataSource.getConnection()) {
			saveMissions(conn, game);
			conn.commit();
		} catch (SQLException e) {
			LOG.e("Failed to save missions", e);
		}
	}

	@Override
	public void moveUnits(Game game, Territory from, Territory to, int move) {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("UPDATE `games` SET `units` = `units` - ? WHERE `gid` = ?");
			statement.setInt(1, move);
			statement.setInt(2, game.getId());
			statement.execute();
			statement = conn.prepareStatement("UPDATE `territories` SET `units` = `units` + ? WHERE `gid` = ? AND `territory` = ?");
			statement.setInt(1, move);
			statement.setInt(2, game.getId());
			statement.setString(3, to.getId());
			statement.execute();
			statement = conn.prepareStatement("UPDATE `territories` SET `units` = `units` - ? WHERE `gid` = ? AND `territory` = ?");
			statement.setInt(1, move);
			statement.setInt(2, game.getId());
			statement.setString(3, from.getId());
			statement.execute();
			conn.commit();
			game.setUnits(game.getUnits() - move);
			from.setUnits(from.getUnits() - move);
			to.setUnits(to.getUnits() + move);
		} catch (SQLException e) {
			LOG.e("Failed to move units", e);
		}
	}

	@Override
	public void playCards(Player player, int units) {
		try(Connection conn = dataSource.getConnection()) {
			long time = System.currentTimeMillis();
			Cards combo = player.getCards().getCombination(units);
			PreparedStatement statement = conn.prepareStatement("UPDATE `gamesjoined` SET `c_art` = `c_art` - ?, `c_cav` = `c_cav` - ?, `c_inf` = `c_inf` - ?, `c_jok` = `c_jok` - ? WHERE `gid` = ? AND `uid` = ?");
			statement.setInt(1, combo.getArtillery());
			statement.setInt(2, combo.getCavalry());
			statement.setInt(3, combo.getInfantry());
			statement.setInt(4, combo.getJokers());
			statement.setInt(5, player.getGame().getId());
			statement.setInt(6, player.getId());
			statement.execute();
			statement = conn.prepareStatement("UPDATE `games` SET `units` = `units` + ?, `time` = ? WHERE `gid` = ?");
			statement.setInt(1, units);
			statement.setLong(2, time);
			statement.setInt(3, player.getGame().getId());
			statement.execute();
			saveLogEntry(conn, new CardsPlayedEntry(player, time, combo.toArray(), units));
			conn.commit();
			player.getCards().removeAll(combo);
			player.getGame().setUnits(player.getGame().getUnits() + units);
			player.getGame().setTime(time);
		} catch (SQLException e) {
			LOG.e("Failed to play cards", e);
		}
	}

	@Override
	public boolean victory(Player player) {
		try(Connection conn = dataSource.getConnection()) {
			Game game = player.getGame();
			deleteCombatLogs(conn, game);
			deleteTerritories(conn, game);
			Player.State playerState = Player.State.VICTORIOUS;
			Game.State gameState = Game.State.FINISHED;
			long time = System.currentTimeMillis();
			setState(conn, player, playerState);
			PreparedStatement statement = conn.prepareStatement("UPDATE `games` SET `state` = ?, `turn` = ?, `time` = ? WHERE `gid` = ?");
			statement.setInt(1, gameState.ordinal());
			statement.setInt(2, 0);
			statement.setLong(3, time);
			statement.setInt(4, game.getId());
			statement.execute();
			conn.commit();
			game.setState(gameState);
			game.setTime(time);
			game.setCurrentTurn(null);
			player.setState(playerState);
			return true;
		} catch (SQLException e) {
			LOG.e("Failed to end game", e);
		}
		return false;
	}

	private void deleteCombatLogs(Connection conn, Game game) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("DELETE FROM `combatlog` WHERE `gid` = ?");
		statement.setInt(1, game.getId());
		statement.execute();
	}

	private void deleteTerritories(Connection conn, Game game) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("DELETE FROM `territories` WHERE `gid` = ?");
		statement.setInt(1, game.getId());
		statement.execute();
	}

	@Override
	public void deleteAttack(Attack attack) {
		Game game = attack.getAttacker().getOwner().getGame();
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM `attacks` WHERE `gid` = ? AND `a_territory` = ? AND `d_territory` = ?");
			statement.setInt(1, game.getId());
			statement.setString(2, attack.getAttacker().getId());
			statement.setString(3, attack.getDefender().getId());
			statement.execute();
			conn.commit();
		} catch (SQLException e) {
			LOG.e("Failed to delete attack", e);
		}
		game.getAttacks().remove(attack);
	}
}