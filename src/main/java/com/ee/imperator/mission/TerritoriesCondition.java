package com.ee.imperator.mission;

import com.ee.imperator.user.Player;

public class TerritoriesCondition implements VictoryCondition {
	private final int numTerritories;

	public TerritoriesCondition(int numTerritories) {
		this.numTerritories = numTerritories;
	}

	@Override
	public boolean isFulfilled(Player player) {
		return player.getTerritories().size() >= numTerritories;
	}
}
