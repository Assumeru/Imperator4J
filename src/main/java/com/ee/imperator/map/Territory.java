package com.ee.imperator.map;

import java.util.ArrayList;
import java.util.List;

import com.ee.imperator.user.Player;

public class Territory implements Comparable<Territory>, HasFlag {
	private final String id;
	private final String name;
	private final List<Region> regions;
	private final List<Territory> borders;
	private Player owner;
	private int units;

	public Territory(String id, String name) {
		this.id = id;
		this.name = name;
		regions = new ArrayList<>();
		borders = new ArrayList<>();
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public List<Territory> getBorders() {
		return borders;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Territory o) {
		return id.compareTo(o.id);
	}

	public Territory copy() {
		return new Territory(id, name);
	}
}
