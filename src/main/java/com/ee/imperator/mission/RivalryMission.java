package com.ee.imperator.mission;

import org.ee.i18n.Language;

import com.ee.imperator.user.Player;

public class RivalryMission extends AbstractMission {
	private EliminateCondition condition;

	public RivalryMission(int id) {
		super(id, "Rivalry", "To win this game you will have to conquer the last of an opponent's territories.");
		this.condition = new EliminateCondition();
	}

	@Override
	public boolean containsEliminate() {
		return true;
	}

	@Override
	public boolean hasBeenCompleted(Player player) {
		return condition.isFulfilled(player);
	}

	@Override
	public String getDescription(Language language, PlayerMission mission) {
		if(mission == null || mission.getTarget() == null) {
			return super.getDescription(language, mission);
		}
		return language.translate("To win this game you will have to conquer the last of %1$s's territories.", mission.getTarget().getName()).toString();
	}
}
