package com.ee.imperator.test.map;

import java.util.Arrays;
import java.util.List;

import com.ee.imperator.map.Map;
import com.ee.imperator.map.MapProvider;

public class MemoryMapProvider implements MapProvider {
	private static final Map INSTANCE = new SimpleMap();

	@Override
	public List<Map> getMaps() {
		return Arrays.asList(INSTANCE);
	}

	@Override
	public Map getMap(int id) {
		if(id == 0) {
			return INSTANCE;
		}
		return null;
	}
}
