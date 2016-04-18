package com.ee.imperator.data;

import java.util.List;

import com.ee.imperator.map.Map;

public interface MapProvider {
	List<Map> getMaps();

	Map getMap(int id);
}
