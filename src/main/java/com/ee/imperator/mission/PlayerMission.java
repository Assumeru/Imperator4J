package com.ee.imperator.mission;

import org.ee.i18n.Language;

import com.ee.imperator.game.Game;
import com.ee.imperator.user.Player;

public class PlayerMission {
	private final Mission mission;
	private final Player player;
	private final int targetId;

	public PlayerMission(Mission mission, Player player, int targetId) {
		if(mission == null) {
			throw new NullPointerException("mission == null");
		} else if(player == null) {
			throw new NullPointerException("player == null");
		}
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
		if(targetId == 0) {
			return null;
		}
		return getGame().getPlayerById(targetId);
	}

	public int getTargetId() {
		return targetId;
	}
}
