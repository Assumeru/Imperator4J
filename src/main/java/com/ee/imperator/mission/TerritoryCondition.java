package com.ee.imperator.mission;

import java.util.Map;

import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public class TerritoryCondition implements VictoryCondition {
	private final String[] ids;

	public TerritoryCondition(String... ids) {
		this.ids = ids;
	}

	@Override
	public boolean isFulfilled(Player player) {
		Map<String, Territory> territories = player.getGame().getMap().getTerritories();
		for(String id : ids) {
			if(!territories.get(id).getOwner().equals(player)) {
				return false;
			}
		}
		return true;
	}
}
