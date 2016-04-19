package com.ee.imperator.user;

import java.util.ArrayList;
import java.util.List;

import com.ee.imperator.game.Cards;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;
import com.ee.imperator.mission.PlayerMission;

public class Player implements User, Comparable<Player> {
	public enum State {
		PLAYING, GAME_OVER, DESTROYED_RIVAL, VICTORIOUS
	}
	private final Member member;
	private Game game;
	private String color;
	private PlayerMission mission;
	private State state;
	private boolean autoroll;
	private final Cards cards;

	public Player(Member member) {
		this(member, null);
	}

	public Player(Member member, Game game) {
		this.member = member;
		this.game = game;
		state = State.PLAYING;
		cards = new Cards();
	}

	@Override
	public int getId() {
		return member.getId();
	}

	@Override
	public String getName() {
		return member.getName();
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setGame(Game game) {
		if(this.game != null) {
			throw new IllegalStateException("Player already has a game");
		}
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public Member getMember() {
		return member;
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

	@Override
	public int compareTo(Player o) {
		return Integer.compare(getId(), o.getId());
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public PlayerMission getMission() {
		return mission;
	}

	public void setMission(PlayerMission mission) {
		this.mission = mission;
	}

	public boolean getAutoRoll() {
		return autoroll;
	}

	public void setAutoRoll(boolean autoroll) {
		this.autoroll = autoroll;
	}

	public Cards getCards() {
		return cards;
	}
}
