package com.ee.imperator.mission;

import org.ee.i18n.Language;

import com.ee.imperator.game.Game;
import com.ee.imperator.user.Player;

public class PlayerMission {
	private final Mission mission;
	private final Player player;
	private final Integer targetId;

	public PlayerMission(Mission mission, Player player, Integer targetId) {
		this.mission = mission;
		this.player = player;
		this.targetId = targetId;
	}

	public String getName() {
		return mission.getName();
	}

	public int getId() {
		return mission.getId();
	}

	public String getDescription(Language language) {
		return mission.getDescription(language, this);
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

	public Game getGame() {
		return player.getGame();
	}

	public Player getTarget() {
		if(targetId == null) {
			return null;
		}
		return getGame().getPlayerById(targetId);
	}
}
