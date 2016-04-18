package org.ee.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {
	private final Map<K, V> map;

	public MapBuilder() {
		this(new HashMap<>());
	}

	public MapBuilder(int capacity) {
		this(new HashMap<>(capacity));
	}

	public MapBuilder(Map<K, V> map) {
		if(map == null) {
			throw new NullPointerException("map cannot be null");
		}
		this.map = map;
	}

	public MapBuilder<K, V> put(K key, V value) {
		map.put(key, value);
		return this;
	}

	public MapBuilder<K, V> putGet(K key, K valueKey) {
		map.put(key, map.get(valueKey));
		return this;
	}

	public MapBuilder<K, V> putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
		return this;
	}

	public Map<K, V> build() {
		return build(false);
	}

	public Map<K, V> build(boolean immutable) {
		if(immutable) {
			return Collections.unmodifiableMap(map);
		}
		return map;
	}
}
