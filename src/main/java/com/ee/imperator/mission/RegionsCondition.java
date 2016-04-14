package com.ee.imperator.mission;

import com.ee.imperator.map.Region;
import com.ee.imperator.user.Player;

public class RegionsCondition implements VictoryCondition {
	private final int numRegions;

	public RegionsCondition(int numRegions) {
		this.numRegions = numRegions;
	}

	@Override
	public boolean isFulfilled(Player player) {
		int regions = 0;
		for(Region region : player.getGame().getMap().getRegions().values()) {
			if(region.isOwnedBy(player)) {
				regions++;
				if(regions >= numRegions) {
					return true;
				}
			}
		}
		return false;
	}
}
