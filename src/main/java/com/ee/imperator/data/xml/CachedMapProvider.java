package com.ee.imperator.data.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.MapProvider;
import com.ee.imperator.map.Map;

public class CachedMapProvider implements MapProvider {
	private java.util.Map<Integer, Map> maps;
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

	@Override
	public void close() throws IOException {
	}
}
