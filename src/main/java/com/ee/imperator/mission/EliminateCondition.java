package com.ee.imperator.mission;

import com.ee.imperator.user.Player;

public class EliminateCondition implements VictoryCondition {
	@Override
	public boolean isFulfilled(Player player) {
		return player.getState() == Player.State.DESTROYED_RIVAL;
	}
}
