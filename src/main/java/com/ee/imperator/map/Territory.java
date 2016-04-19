package com.ee.imperator.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ee.imperator.user.Player;

public class Territory implements Comparable<Territory>, HasFlag, Cloneable {
	private final String id;
	private final String name;
	private final List<Region> regions;
	private final Set<Territory> borders;
	private Player owner;
	private int units;

	public Territory(String id, String name) {
		this.id = id;
		this.name = name;
		regions = new ArrayList<>();
		borders = new HashSet<>();
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

	public Set<Territory> getBorders() {
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

	@Override
	public Territory clone() {
		return new Territory(id, name);
	}
}
