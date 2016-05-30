package com.ee.imperator.mission;

import com.ee.imperator.user.Player;

@FunctionalInterface
public interface VictoryCondition {
	public boolean isFulfilled(Player player);
}
