package com.ee.imperator.game;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ee.crypt.Hasher;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

import com.ee.imperator.Imperator;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;

public class Game implements Comparable<Game> {
	private static final Logger LOG = LogManager.createLogger();

	public enum State {
		TURN_START, FORTIFY, POST_COMBAT, FINISHED
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

	public Game(int id, Map map, String name, Player owner, String password, long time) {
		this.id = id;
		this.map = map;
		this.name = name;
		this.owner = owner;
		players = new ArrayList<>();
		addPlayer(owner);
		this.time = time;
		this.password = password;
		state = State.TURN_START;
	}

	public Game(int id, Map map, String name, int owner, int turn, long time, State state, int units, boolean conquered, String password, Collection<Player> players) {
		this.id = id;
		this.map = map;
		this.name = name;
		this.time = time;
		this.state = state;
		this.units = units;
		this.conquered = conquered;
		this.password = password;
		this.players = new ArrayList<>(players.size());
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

	@Override
	public int compareTo(Game o) {
		int c = map.compareTo(o.map);
		if(c == 0) {
			return Integer.compare(players.size(), o.players.size());
		}
		return c;
	}
}
