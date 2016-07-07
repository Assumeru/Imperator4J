package com.ee.imperator.map;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ee.imperator.mission.Mission;
import com.ee.imperator.user.Player;

public class Map implements Comparable<Map> {
	private final int id;
	private final String name;
	private final int players;
	private final java.util.Map<String, String> descriptions;
	private final SortedMap<String, Territory> territories;
	private final SortedMap<String, Region> regions;
	private final java.util.Map<Integer, Mission> missions;
	private final List<Integer> missionDistribution;

	public Map(int id, String name, int players, java.util.Map<String, String> descriptions, SortedMap<String, Territory> territories, SortedMap<String, Region> regions, java.util.Map<Integer, Mission> missions, List<Integer> missionDistribution) {
		this.id = id;
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

	public int getId() {
		return id;
	}

	public String getDescription(String lang) {
		lang = lang.toLowerCase(Locale.US);
		if(descriptions.containsKey(lang)) {
			return descriptions.get(lang);
		} else {
			String description = getMatchingDescription(lang);
			if(description != null) {
				return description;
			}
		}
		return descriptions.values().iterator().next();
	}

	private String getMatchingDescription(String lang) {
		int maxMatches = -1;
		String bestMatch = null;
		String[] langBits = lang.split("-");
		for(Entry<String, String> entry : descriptions.entrySet()) {
			String[] keyBits = entry.getKey().toLowerCase(Locale.US).split("-");
			for(int i = 0; i < langBits.length && i < keyBits.length; i++) {
				if(!langBits[i].equals(keyBits[i])) {
					break;
				} else if(i > maxMatches) {
					maxMatches = i;
					bestMatch = entry.getValue();
				}
			}
		}
		if(maxMatches > -1) {
			return bestMatch;
		}
		return null;
	}

	@Override
	public int compareTo(Map o) {
		int diff = name.compareTo(o.name);
		if(diff == 0) {
			return Integer.compare(players, o.players);
		}
		return diff;
	}

	public Map copy() {
		SortedMap<String, Territory> copyTerritories = new TreeMap<>();
		SortedMap<String, Region> copyRegions = new TreeMap<>();
		for(Territory territory : territories.values()) {
			copyTerritories.put(territory.getId(), territory.copy());
		}
		for(Territory territory : territories.values()) {
			Territory clone = copyTerritories.get(territory.getId());
			for(Territory border : territory.getBorders()) {
				clone.getBorders().add(copyTerritories.get(border.getId()));
			}
		}
		for(Region region : regions.values()) {
			Region clone = region.copy();
			copyRegions.put(region.getId(), clone);
			for(Territory territory : region.getTerritories()) {
				Territory clonedTerritory = copyTerritories.get(territory.getId());
				clone.getTerritories().add(clonedTerritory);
				clonedTerritory.getRegions().add(clone);
			}
		}
		for(Territory territory : copyTerritories.values()) {
			territory.getRegions().sort(null);
		}
		return new Map(id, name, players, descriptions, copyTerritories, copyRegions, missions, missionDistribution);
	}

	public int getNumberOfTerritories(Player player) {
		int sum = 0;
		for(Territory territory : territories.values()) {
			if(territory.getOwner().equals(player)) {
				sum++;
			}
		}
		return sum;
	}
}
