package com.ee.imperator.game;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.ee.collection.FixedSizeList;
import org.ee.collection.Util;
import org.ee.collection.VariableSizeList;
import org.ee.crypt.Hasher;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

import com.ee.imperator.Imperator;
import com.ee.imperator.game.Cards.Card;
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

	private int id;
	private String name;
	private State state;
	private Map map;
	private List<Player> players;
	private Player currentTurn;
	private String password;
	private Player owner;
	private long time;
	private int units;
	private boolean conquered;
	private String inviteCode;
	private List<Attack> attacks;

	private Game(int id, Map map, String name, String password, long time) {
		this.id = id;
		this.map = map;
		this.name = name;
		this.password = password;
		this.time = time;
		players = new FixedSizeList<>(map.getPlayers());
		attacks = new VariableSizeList<>();
	}

	public Game(int id, Map map, String name, Player owner, String password, long time) {
		this(id, map, name, password, time);
		this.owner = owner;
		addPlayer(owner);
		state = State.TURN_START;
	}

	public Game(int id, Map map, String name, int owner, int turn, long time, State state, int units, boolean conquered, String password, Collection<Player> players) {
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

	public void addPlayer(Player player) {
		addPlayer(player, true);
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

	private void addPlayer(Player player, boolean sort) {
		if(!players.contains(player)) {
			player.setGame(this);
			players.add(player);
			if(sort) {
				players.sort(null);
			}
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

	public List<Attack> getAttacks() {
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

	public void start() {
		Random random = new Random();
		distributeTerritories(random);
		distributeMissions(random);
		currentTurn = players.get(random.nextInt(players.size()));
		time = System.currentTimeMillis();
	}

	private void distributeTerritories(Random random) {
		Territory[] territories = map.getTerritories().values().toArray(new Territory[map.getTerritories().size()]);
		Util.shuffle(territories, random);
		int perPlayer = territories.length / players.size();
		int t = 0;
		for(Player player : players) {
			for(int i = 0; i < perPlayer; i++, t++) {
				territories[t].setOwner(player);
				territories[t].setUnits(INITIAL_UNITS);
			}
		}
	}

	private void distributeMissions(Random random) {
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
			player.setMission(new PlayerMission(mission, player, target));
		}
	}

	public Card giveCard(Player player, Card card) {
		if(card != null && player.getCards().contains(card) || player.getCards().size() < Cards.MAX_CARDS) {
			Card random = Card.getRandom(player.getCards());
			if(Imperator.getState().addCards(player, random, 1)) {
				return random;
			}
		}
		return null;
	}

	public void nextTurn() {
		if(currentTurn.getMission().hasBeenCompleted()) {
			victory(currentTurn);
			return;
		}
		Player next;
		int i = (players.indexOf(currentTurn) + 1) % players.size();
		while(true) {
			Player player = players.get(i);
			if(player.equals(currentTurn)) {
				victory(currentTurn);
				return;
			} else if(player.getState() != Player.State.GAME_OVER) {
				next = player;
				break;
			}
			i = (i + 1) % players.size();
		}
		Imperator.getState().startTurn(next);
	}

	private void victory(Player winner) {
		if(Imperator.getState().victory(winner)) {
			for(Player player : players) {
				if(player.equals(winner)) {
					Imperator.getState().addWin(player.getMember(), players.size() - 1);
				} else {
					Imperator.getState().addLoss(player.getMember());
				}
			}
		}
	}

	public void forfeit(Player player) {
		Imperator.getState().forfeit(player);
		int numRemaining = 0;
		Player last = null;
		for(Player remaining : players) {
			if(remaining.getState() != Player.State.GAME_OVER) {
				numRemaining++;
				last = remaining;
			}
		}
		if(numRemaining < 2) {
			victory(last);
		} else if(currentTurn.equals(player)) {
			nextTurn();
		}
	}

	public void executeAttack(Attack attack) {
		Player defender = attack.getDefender().getOwner();
		Imperator.getState().attack(this, attack);
		if(attack.getAttacker().getOwner().equals(attack.getDefender().getOwner())) {
			for(Territory territory : map.getTerritories().values()) {
				if(territory.getOwner().equals(defender)) {
					return;
				}
			}
			boolean newMissions = false;
			Imperator.getState().setState(defender, Player.State.GAME_OVER);
			for(Player player : players) {
				if(player.getMission().containsEliminate() && defender.equals(player.getMission().getTarget())) {
					if(player.equals(attack.getAttacker().getOwner())) {
						Imperator.getState().setState(player, Player.State.DESTROYED_RIVAL);
					} else {
						player.setMission(new PlayerMission(map.getMissions().get(player.getMission().getFallback()), player, 0));
						newMissions = true;
					}
				}
			}
			if(newMissions) {
				Imperator.getState().saveMissions(this);
			}
		}
	}
}
