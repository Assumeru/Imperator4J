package com.ee.imperator.mission;

import org.ee.i18n.Language;

import com.ee.imperator.user.Player;

public class DominationMission extends AbstractMission {
	private final TerritoriesCondition condition;

	public DominationMission(int id, int numTerritories) {
		super(id, "Domination", "To win this game you will have to conquer %1$d territories.");
		this.condition = new TerritoriesCondition(numTerritories);
	}

	@Override
	public boolean containsEliminate() {
		return false;
	}

	@Override
	public boolean hasBeenCompleted(Player player) {
		return condition.isFulfilled(player);
	}

	@Override
	public String getDescription(Language language, PlayerMission mission) {
		return language.translate(getDescription(), condition.getNumTerritories()).toString();
	}
}
