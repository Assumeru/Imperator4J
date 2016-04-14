package com.ee.imperator.map;

import java.util.ArrayList;
import java.util.List;

public class Region {
	private final String id;
	private final String name;
	private final int units;
	private final List<Territory> territories;

	public Region(String id, String name, int units) {
		this.id = id;
		this.name = name;
		this.units = units;
		this.territories = new ArrayList<>();
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

	public List<Territory> getTerritories() {
		return territories;
	}
}
