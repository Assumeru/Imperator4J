package com.ee.imperator.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ee.imperator.Imperator;

public class CachedMapProvider implements MapProvider {
	private volatile java.util.Map<Integer, Map> maps;
	private List<Map> sortedMaps;

	@Override
	public List<Map> getMaps() {
		if(sortedMaps == null) {
			ArrayList<Map> maps = new ArrayList<>(maps().values());
			maps.sort(null);
			maps.trimToSize();
			sortedMaps = Collections.unmodifiableList(maps);
		}
		return sortedMaps;
	}

	@Override
	public Map getMap(int id) {
		return maps().get(id);
	}

	private java.util.Map<Integer, Map> maps() {
		if(maps == null) {
			loadMaps();
		}
		return maps;
	}

	private synchronized void loadMaps() {
		if(maps == null) {
			maps = MapParser.parseMaps(Imperator.getFiles(Imperator.getConfig().getString(getClass(), "path"), ".xml"));
		}
	}
}
