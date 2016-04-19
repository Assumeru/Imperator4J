package com.ee.imperator.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;

public class Game implements Comparable<Game> {
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

	public Game(int id, Map map, String name, int owner, int turn, long time, State state, int units, boolean conquered, String password, Collection<Player> players) {
		this.id = id;
		this.name = name;
		this.time = time;
		this.state = state;
		this.units = units;
		this.conquered = conquered;
		this.password = password;
		this.players = new ArrayList<>(players.size());
		for(Player player : players) {
			player.setGame(this);
			this.players.add(player);
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

	@Override
	public int compareTo(Game o) {
		int c = map.compareTo(o.map);
		if(c == 0) {
			return Integer.compare(players.size(), o.players.size());
		}
		return c;
	}
}
