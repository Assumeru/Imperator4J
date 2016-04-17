package com.ee.imperator.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;

public class Game {
	public enum State {
		TURN_START, FORTIFY, POST_COMBAT, FINISHED
	}
	private int id;
	private State state;
	private Map map;
	private List<Player> players;
	private Player currentTurn;
	private String password;

	public Game(Map map) {
		this.map = map;
		players = new ArrayList<>();
		state = State.TURN_START;
	}

	public int getId() {
		return id;
	}

	public Map getMap() {
		return map;
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
}
