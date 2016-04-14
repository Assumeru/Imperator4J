package com.ee.imperator.map;

import java.util.HashSet;
import java.util.Set;

import com.ee.imperator.user.User;

public class Region implements Comparable<Region> {
	private final String id;
	private final String name;
	private final int units;
	private final Set<Territory> territories;

	public Region(String id, String name, int units) {
		this.id = id;
		this.name = name;
		this.units = units;
		this.territories = new HashSet<>();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getUnits() {
		return units;
	}

	public Set<Territory> getTerritories() {
		return territories;
	}

	public boolean isOwnedBy(User user) {
		for(Territory territory : territories) {
			if(!territory.getOwner().equals(user)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(Region o) {
		return id.compareTo(o.id);
	}
}
