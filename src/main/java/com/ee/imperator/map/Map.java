package com.ee.imperator.map;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ee.imperator.mission.Mission;

public class Map implements Comparable<Map> {
	private int id;
	private String name;
	private int players;
	private java.util.Map<String, String> descriptions;
	private SortedMap<String, Territory> territories;
	private SortedMap<String, Region> regions;
	private java.util.Map<Integer, Mission> missions;
	private List<Integer> missionDistribution;

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
		Map map = new Map(id, name, players, descriptions, null, null, missions, missionDistribution);
		map.territories = new TreeMap<>();
		for(Territory territory : territories.values()) {
			map.territories.put(territory.getId(), territory.copy());
		}
		for(Territory territory : territories.values()) {
			Territory clone = map.territories.get(territory.getId());
			for(Territory border : territory.getBorders()) {
				clone.getBorders().add(map.territories.get(border.getId()));
			}
		}
		map.regions = new TreeMap<>();
		for(Region region : regions.values()) {
			Region clone = region.copy();
			map.regions.put(region.getId(), clone);
			for(Territory territory : region.getTerritories()) {
				Territory clonedTerritory = map.territories.get(territory.getId());
				clone.getTerritories().add(clonedTerritory);
				clonedTerritory.getRegions().add(clone);
			}
		}
		for(Territory territory : map.territories.values()) {
			territory.getRegions().sort(null);
		}
		return map;
	}
}
