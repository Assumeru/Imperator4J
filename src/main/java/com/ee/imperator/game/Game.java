package com.ee.imperator.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ee.imperator.map.Map;
import com.ee.imperator.user.Member;
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

	public Game(Member owner, String name, Map map) {
		this.owner = new Player(owner, this);
		this.name = name;
		this.map = map;
		players = new ArrayList<>();
		players.add(this.owner);
		state = State.TURN_START;
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

	@Override
	public int compareTo(Game o) {
		int c = map.compareTo(o.map);
		if(c == 0) {
			return Integer.compare(players.size(), o.players.size());
		}
		return c;
	}
}
