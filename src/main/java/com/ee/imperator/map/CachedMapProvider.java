package com.ee.imperator.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ee.imperator.ImperatorApplicationContext;

public class CachedMapProvider implements MapProvider {
	private java.util.Map<Integer, Map> maps;
	private List<Map> sortedMaps;

	public CachedMapProvider(ImperatorApplicationContext context) {
		maps = MapParser.parseMaps(context.getFiles(context.getConfig().getString(getClass(), "path"), ".xml"));
		ArrayList<Map> sorted = new ArrayList<>(maps.values());
		sorted.sort(null);
		sorted.trimToSize();
		sortedMaps = Collections.unmodifiableList(sorted);
	}

	@Override
	public List<Map> getMaps() {
		return sortedMaps;
	}

	@Override
	public Map getMap(int id) {
		return maps.get(id);
	}
}
