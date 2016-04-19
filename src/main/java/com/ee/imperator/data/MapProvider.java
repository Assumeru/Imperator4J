package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import com.ee.imperator.map.Map;

public interface MapProvider extends Closeable {
	List<Map> getMaps();

	Map getMap(int id);
}
