package com.ee.imperator.mission;

import java.util.Map;

import com.ee.imperator.map.Region;
import com.ee.imperator.user.Player;

public class RegionCondition implements VictoryCondition {
	private final String[] ids;

	public RegionCondition(String... ids) {
		this.ids = ids;
	}

	@Override
	public boolean isFulfilled(Player player) {
		Map<String, Region> regions = player.getGame().getMap().getRegions();
		for(String id : ids) {
			if(!regions.get(id).isOwnedBy(player)) {
				return false;
			}
		}
		return true;
	}
}
