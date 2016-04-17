package com.ee.imperator.map;

public interface HasFlag {
	String getId();

	default String getPath() {
		return "flags/" + getId().replaceAll("_", "/") + ".png";
	}
}
