package com.ee.imperator.map;

import java.util.List;

import com.ee.imperator.mission.Mission;

public class Map {
	private final String name;
	private final int players;
	private final java.util.Map<String, String> descriptions;
	private final java.util.Map<String, Territory> territories;
	private final java.util.Map<String, Region> regions;
	private final java.util.Map<Integer, Mission> missions;
	private final List<Integer> missionDistribution;

	public Map(String name, int players, java.util.Map<String, String> descriptions, java.util.Map<String, Territory> territories, java.util.Map<String, Region> regions, java.util.Map<Integer, Mission> missions, List<Integer> missionDistribution) {
		this.name = name;
		this.players = players;
		this.descriptions = descriptions;
		this.territories = territories;
		this.regions = regions;
		this.missions = missions;
		this.missionDistribution = missionDistribution;
	}

	public String getName() {
		return name;
	}

	public int getPlayers() {
		return players;
	}

	public java.util.Map<String, String> getDescriptions() {
		return descriptions;
	}

	public java.util.Map<String, Territory> getTerritories() {
		return territories;
	}

	public java.util.Map<String, Region> getRegions() {
		return regions;
	}

	public java.util.Map<Integer, Mission> getMissions() {
		return missions;
	}

	public List<Integer> getMissionDistribution() {
		return missionDistribution;
	}
}
