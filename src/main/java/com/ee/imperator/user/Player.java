package com.ee.imperator.user;

import java.util.ArrayList;
import java.util.List;

import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;

public class Player implements User {
	public enum State {
		PLAYING, GAME_OVER, DESTROYED_RIVAL, VICTORIOUS
	}
	private final Member member;
	private final Game game;
	private State state;

	public Player(Member member, Game game) {
		this.member = member;
		this.game = game;
		state = State.PLAYING;
	}

	@Override
	public int getId() {
		return member.getId();
	}

	@Override
	public String getName() {
		return member.getName();
	}

	@Override
	public String getProfileLink() {
		return member.getProfileLink();
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Game getGame() {
		return game;
	}

	public List<Territory> getTerritories() {
		List<Territory> territories = new ArrayList<>();
		for(Territory territory : game.getMap().getTerritories().values()) {
			if(equals(territory.getOwner())) {
				territories.add(territory);
			}
		}
		return territories;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(obj instanceof User) {
			return ((User) obj).getId() == getId();
		}
		return false;
	}
}
