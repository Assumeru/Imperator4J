package com.ee.imperator.request.context;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class MapVariable extends Variable<Map<String, Object>> {
	private Map<PageContext, Map<String, Object>> values = new WeakHashMap<>();

	public MapVariable(String name) {
		super(name, null);
	}

	public Map<String, Object> get(PageContext context) {
		Map<String, Object> value = values.get(context);
		if(value == null) {
			value = new HashMap<>(0);
			values.put(context, value);
		}
		return value;
	}

	@Override
	public Map<String, Object> getDefaultValue(PageContext context) {
		throw new UnsupportedOperationException();
	}

	public Object put(PageContext context, String key, Object value) {
		return get(context).put(key, value);
	}
}
