package com.ee.imperator.mission;

import org.ee.i18n.Language;

import com.ee.imperator.user.Player;

public class PlayerMission {
	private final Mission mission;
	private final Player player;

	public PlayerMission(Mission mission, Player player) {
		this.mission = mission;
		this.player = player;
	}

	public String getName() {
		return mission.getName();
	}

	public int getId() {
		return mission.getId();
	}

	public String getDescription(Language language) {
		return mission.getDescription(language);
	}

	public boolean containsEliminate() {
		return mission.containsEliminate();
	}

	public Integer getFallback() {
		return mission.getFallback();
	}

	public boolean hasBeenCompleted() {
		return mission.hasBeenCompleted(player);
	}
}
