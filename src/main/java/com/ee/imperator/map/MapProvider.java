package com.ee.imperator.map;

import java.util.List;

public interface MapProvider {
	List<Map> getMaps();

	Map getMap(int id);
}
