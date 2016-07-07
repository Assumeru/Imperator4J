package com.ee.imperator.game;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import org.ee.collection.FixedSizeList;
import org.ee.collection.Util;
import org.ee.crypt.Hasher;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.data.transaction.PlayerTransaction;
import com.ee.imperator.data.transaction.TerritoryTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.game.log.AttackedEntry;
import com.ee.imperator.game.log.ConqueredEntry;
import com.ee.imperator.game.log.EndedTurnEntry;
import com.ee.imperator.game.log.ForfeitedEntry;
import com.ee.imperator.map.Map;
import com.ee.imperator.map.Territory;
import com.ee.imperator.mission.Mission;
import com.ee.imperator.mission.PlayerMission;
import com.ee.imperator.user.Player;

public class Game implements Comparable<Game> {
	private static final Logger LOG = LogManager.createLogger();
	public static final int MAX_MOVE_UNITS = 7;
	public static final int MIN_FORTIFY = 3;
	public static final int MAX_ATTACKERS = 3;
	public static final int MAX_DEFENDERS = 2;
	public static final int INITIAL_UNITS = 3;
	public enum State {
		TURN_START, FORTIFY, COMBAT, POST_COMBAT, FINISHED
	}

	private final int id;
	private final String name;
	private State state;
	private final Map map;
	private final List<Player> players;
	private Player currentTurn;
	private final String password;
	private Player owner;
	private long time;
	private int units;
	private boolean conquered;
	private volatile String inviteCode;
	private final Set<Attack> attacks;

	private Game(int id, Map map, String name, String password, long time) {
		this.id = id;
		this.map = map;
		this.name = name;
		this.password = password;
		this.time = time;
		players = new FixedSizeList<>(map.getPlayers());
		attacks = Collections.synchronizedSet(new HashSet<>());
	}

	public Game(int id, Map map, String name, Player owner, String password, long time) {
		this(id, map, name, password, time);
		this.owner = owner;
		addPlayer(owner, false);
		state = State.TURN_START;
	}

	public Game(int id, Map map, String name, int owner, int turn, long time, State state, int units, boolean conquered, String password, Iterable<Player> players) {
		this(id, map, name, password, time);
		this.state = state;
		this.units = units;
		this.conquered = conquered;
		for(Player player : players) {
			addPlayer(player, false);
			if(player.getId() == owner) {
				this.owner = player;
			}
			if(player.getId() == turn) {
				currentTurn = player;
			}
		}
		this.players.sort(null);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Map getMap() {
		return map;
	}

	public Player getOwner() {
		return owner;
	}

	public void addPlayer(Player player) throws TransactionException {
		synchronized(players) {
			if(!players.contains(player)) {
				if(hasStarted() || hasEnded()) {
					throw new IllegalStateException("Cannot add players after starting");
				} else if(players.size() >= map.getPlayers()) {
					throw new IllegalStateException("Game is full");
				}
				try(GameTransaction transaction = Imperator.getState().modify(this)) {
					transaction.addPlayer(player);
					transaction.setTime(System.currentTimeMillis());
					transaction.commit();
				}
				addPlayer(player, true);
			}
		}
	}

	public void removePlayer(Player player) throws TransactionException {
		synchronized(players) {
			if(hasStarted() || hasEnded()) {
				throw new IllegalStateException("Cannot remove players after starting");
			}
			try(GameTransaction transaction = Imperator.getState().modify(this)) {
				transaction.removePlayer(player);
				transaction.setTime(System.currentTimeMillis());
				transaction.commit();
			}
			players.remove(player);
		}
	}

	private void addPlayer(Player player, boolean sort) {
		player.setGame(this);
		players.add(player);
		if(sort) {
			players.sort(null);
		}
	}

	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	public Player getCurrentPlayer() {
		return currentTurn;
	}

	public Player getPlayerById(int id) {
		for(Player player : players) {
			if(player.getId() == id) {
				return player;
			}
		}
		return null;
	}

	public boolean hasStarted() {
		return currentTurn != null;
	}

	public boolean hasPassword() {
		return password != null;
	}

	public boolean hasEnded() {
		return state == State.FINISHED;
	}

	public boolean hasConquered() {
		return conquered;
	}

	public long getTime() {
		return time;
	}

	public int getUnits() {
		return units;
	}

	public String getInviteCode() {
		if(password == null) {
			return null;
		} else if(inviteCode == null) {
			generateInviteCode();
		}
		return inviteCode;
	}

	private synchronized void generateInviteCode() {
		if(inviteCode == null) {
			try {
				byte[] input = (id + password).getBytes("UTF-8");
				inviteCode = new Hasher(Imperator.getConfig().getString(getClass(), "inviteCode", "MD5")).digest(input).toString(16);
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				LOG.e(e);
			}
		}
	}

	public String getPassword() {
		return password;
	}

	public State getState() {
		return state;
	}

	public Set<Attack> getAttacks() {
		return attacks;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setCurrentTurn(Player currentTurn) {
		this.currentTurn = currentTurn;
	}

	public void setConquered(boolean conquered) {
		this.conquered = conquered;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	@Override
	public int compareTo(Game o) {
		int c = map.compareTo(o.map);
		if(c == 0) {
			return Integer.compare(players.size(), o.players.size());
		}
		return c;
	}

	public void start() throws TransactionException {
		synchronized(players) {
			if(players.size() != map.getPlayers()) {
				throw new IllegalStateException("Incorrect player amount");
			}
			Random random = new Random();
			try(GameTransaction transaction = Imperator.getState().modify(this)) {
				distributeTerritories(random, transaction);
				distributeMissions(random, transaction);
				transaction.setCurrentTurn(players.get(random.nextInt(players.size())));
				transaction.setTime(System.currentTimeMillis());
				transaction.commit();
			}
		}
	}

	private void distributeTerritories(Random random, GameTransaction parentTransaction) throws TransactionException {
		Territory[] territories = map.getTerritories().values().toArray(new Territory[map.getTerritories().size()]);
		Util.shuffle(territories, random);
		int perPlayer = territories.length / players.size();
		int t = 0;
		for(Player player : players) {
			for(int i = 0; i < perPlayer; i++, t++) {
				TerritoryTransaction transaction = parentTransaction.getTerritory(territories[t]);
				transaction.setOwner(player);
				transaction.setUnits(INITIAL_UNITS);
			}
		}
	}

	private void distributeMissions(Random random, GameTransaction parentTransaction) throws TransactionException {
		Integer[] distribution = map.getMissionDistribution().toArray(new Integer[map.getMissionDistribution().size()]);
		Util.shuffle(distribution, random);
		int i = 0;
		for(Player player : players) {
			int target = 0;
			Mission mission = map.getMissions().get(distribution[i++]);
			if(mission.containsEliminate()) {
				target = random.nextInt(players.size() - 1);
				if(players.get(target).equals(player)) {
					target = players.size() - 1;
				}
				target = players.get(target).getId();
			}
			PlayerTransaction transaction = parentTransaction.getPlayer(player);
			transaction.setMission(new PlayerMission(mission, player, target));
		}
	}

	public Card endTurn(Card discard) throws TransactionException {
		try(GameTransaction transaction = Imperator.getState().modify(this)) {
			if(conquered && (discard != null && currentTurn.getCards().contains(discard) || currentTurn.getCards().size() < Cards.MAX_CARDS)) {
				Card random = Card.getRandom(currentTurn.getCards());
				transaction.getPlayer(currentTurn).getCards().add(random);
			}
			nextTurn(transaction);
			transaction.commit();
		}
		return null;
	}

	private void nextTurn(GameTransaction transaction) throws TransactionException {
		if(currentTurn.getMission().hasBeenCompleted()) {
			victory(currentTurn, transaction);
			return;
		}
		Player next;
		int i = (players.indexOf(currentTurn) + 1) % players.size();
		while(true) {
			Player player = players.get(i);
			if(player.equals(currentTurn)) {
				victory(currentTurn, transaction);
				return;
			} else if(player.getState() != Player.State.GAME_OVER) {
				next = player;
				break;
			}
			i = (i + 1) % players.size();
		}
		long time = System.currentTimeMillis();
		transaction.setConquered(false);
		transaction.setState(State.TURN_START);
		transaction.setTime(time);
		transaction.setCurrentTurn(next);
		transaction.setUnits(next.getUnitsFromRegionsPerTurn());
		transaction.addEntry(new EndedTurnEntry(currentTurn, time));
	}

	private void victory(Player winner, GameTransaction transaction) throws NoSuchElementException, TransactionException {
		transaction.deleteLogEntries();
		transaction.deleteTerritories();
		PlayerTransaction playerTransaction = transaction.getPlayer(winner);
		playerTransaction.setState(Player.State.VICTORIOUS);
		playerTransaction.addWin();
		transaction.setState(State.FINISHED);
		transaction.setTime(System.currentTimeMillis());
		transaction.setCurrentTurn(null);
		for(Player player : players) {
			if(!player.equals(winner)) {
				transaction.getPlayer(player).addLoss();
			}
		}
	}

	public void forfeit(Player player) throws TransactionException {
		try(GameTransaction transaction = Imperator.getState().modify(this)) {
			PlayerTransaction playerTransaction = transaction.getPlayer(player);
			playerTransaction.setState(Player.State.GAME_OVER);
			playerTransaction.setAutoRoll(true);
			long time = System.currentTimeMillis();
			transaction.addEntry(new ForfeitedEntry(player, time));
			transaction.setTime(time);
			int numRemaining = 0;
			Player last = null;
			for(Player remaining : players) {
				if(remaining.getState() != Player.State.GAME_OVER && !remaining.equals(player)) {
					numRemaining++;
					last = remaining;
				}
			}
			if(numRemaining < 2) {
				victory(last, transaction);
			} else if(currentTurn.equals(player)) {
				nextTurn(transaction);
			}
			transaction.commit();
		}
	}

	public void executeAttack(Attack attack, GameTransaction transaction) throws TransactionException {
		Player defender = attack.getDefender().getOwner();
		if(attack(attack, transaction) && map.getNumberOfTerritories(defender) == 1) {
			transaction.getPlayer(defender).setState(Player.State.GAME_OVER);
			for(Player player : players) {
				if(player.getMission().containsEliminate() && defender.equals(player.getMission().getTarget())) {
					PlayerTransaction playerTransaction = transaction.getPlayer(player);
					if(player.equals(attack.getAttacker().getOwner())) {
						playerTransaction.setState(Player.State.DESTROYED_RIVAL);
					} else {
						playerTransaction.setMission(new PlayerMission(map.getMissions().get(player.getMission().getFallback()), player, 0));
					}
				}
			}
		}
	}

	private boolean attack(Attack attack, GameTransaction transaction) throws TransactionException {
		long time = System.currentTimeMillis();
		Player aOwner = attack.getAttacker().getOwner();
		Player dOwner = attack.getDefender().getOwner();
		transaction.addEntry(new AttackedEntry(time, aOwner, dOwner, attack.getAttackRoll(), attack.getDefendRoll(), attack.getAttacker(), attack.getDefender()));
		int attackerUnits = attack.getAttacker().getUnits() - attack.getAttackLosses();
		int defenderUnits = attack.getDefender().getUnits() - attack.getDefendLosses();
		boolean conquered = defenderUnits < 1;
		transaction.setTime(time);
		TerritoryTransaction attackerTransaction = transaction.getTerritory(attack.getAttacker());
		TerritoryTransaction defenderTransaction = transaction.getTerritory(attack.getDefender());
		if(conquered) {
			transaction.addEntry(new ConqueredEntry(aOwner, time, attack.getDefender()));
			transaction.setConquered(true);
			defenderTransaction.setOwner(aOwner);
			int move = attack.getMove();
			if(move >= attackerUnits) {
				move = attackerUnits - 1;
			}
			attackerUnits -= move;
			defenderUnits = move;
		}
		attackerTransaction.setUnits(attackerUnits);
		defenderTransaction.setUnits(defenderUnits);
		return conquered;
	}

	public void moveUnits(Territory from, Territory to, int move) throws TransactionException {
		try(GameTransaction transaction = Imperator.getState().modify(this)) {
			transaction.setUnits(units - move);
			transaction.getTerritory(from).setUnits(from.getUnits() - move);
			transaction.getTerritory(to).setUnits(to.getUnits() + move);
			transaction.commit();
		}
	}

	public void defend(Attack attack, int units) throws TransactionException {
		attack.rollDefence(units);
		try(GameTransaction transaction = Imperator.getState().modify(this)) {
			executeAttack(attack, transaction);
			transaction.getAttacks().remove(attack);
			transaction.commit();
		}
	}
}
