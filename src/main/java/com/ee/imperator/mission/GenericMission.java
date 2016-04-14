package com.ee.imperator.mission;

import java.util.List;

import com.ee.imperator.user.Player;

public class GenericMission extends AbstractMission {
	private final List<VictoryCondition> conditions;

	public GenericMission(int id, String name, String description, List<VictoryCondition> conditions) {
		super(id, name, description);
		this.conditions = conditions;
	}

	@Override
	public boolean containsEliminate() {
		for(VictoryCondition condition : conditions) {
			if(condition instanceof EliminateCondition) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasBeenCompleted(Player player) {
		for(VictoryCondition condition : conditions) {
			if(!condition.isFulfilled(player)) {
				return false;
			}
		}
		return true;
	}
}
