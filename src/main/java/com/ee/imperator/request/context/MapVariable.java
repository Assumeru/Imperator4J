package com.ee.imperator.request.context;

import java.util.HashMap;
import java.util.Map;

public class MapVariable<K, V> extends VariableVariable<Map<K, V>> {
	public MapVariable(String name) {
		super(name);
	}

	@Override
	protected Map<K, V> newInstance() {
		return new HashMap<>(0);
	}

	public V put(PageContext context, K key, V value) {
		return getDefaultValue(context).put(key, value);
	}
}
