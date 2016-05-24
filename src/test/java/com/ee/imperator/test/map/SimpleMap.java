package com.ee.imperator.test.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ee.imperator.map.Map;
import com.ee.imperator.map.Region;
import com.ee.imperator.map.Territory;
import com.ee.imperator.mission.DominationMission;
import com.ee.imperator.mission.Mission;

public class SimpleMap extends Map {
	private static final SortedMap<String, Territory> TERRITORIES = new TreeMap<>();
	private static final SortedMap<String, Region> REGIONS = new TreeMap<>();
	private static final java.util.Map<Integer, Mission> MISSIONS = new HashMap<>();
	private static final List<Integer> MISSION_DISTRIBUTION = Arrays.asList(0, 0, 0);
	static {
		Territory territory1 = new Territory("t1", "Test Territory 1");
		Territory territory2 = new Territory("t2", "Test Territory 2");
		Territory territory3 = new Territory("t3", "Test Territory 3");
		territory1.getBorders().add(territory2);
		territory1.getBorders().add(territory3);
		territory2.getBorders().add(territory1);
		territory2.getBorders().add(territory3);
		territory3.getBorders().add(territory1);
		territory3.getBorders().add(territory2);
		Region region = new Region("region", "Test Region", 1);
		territory1.getRegions().add(region);
		territory2.getRegions().add(region);
		region.getTerritories().add(territory1);
		region.getTerritories().add(territory2);
		TERRITORIES.put(territory1.getId(), territory1);
		TERRITORIES.put(territory2.getId(), territory2);
		TERRITORIES.put(territory3.getId(), territory3);
		REGIONS.put(region.getId(), region);
		MISSIONS.put(0, new DominationMission(0, 3));
	}

	public SimpleMap() {
		super(0, "Test Map", 3, Collections.emptyMap(), TERRITORIES, REGIONS, MISSIONS, MISSION_DISTRIBUTION);
	}
}
