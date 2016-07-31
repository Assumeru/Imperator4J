package org.ee.web.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ee.collection.ListMap;
import org.ee.collection.MapWrapper;

public class ParamMap extends MapWrapper<String, List<String>> implements ListMap<String, String> {
	public static final ParamMap INSTANCE = new ParamMap();

	private ParamMap() {
		super(Collections.emptyMap());
	}

	public ParamMap(Map<String, List<String>> map) {
		super(Collections.unmodifiableMap(map));
    }

	public static ParamMap of(Map<String, String[]> params) {
		Map<String, List<String>> map = new HashMap<>();
		for(Entry<String, String[]> entry : params.entrySet()) {
			List<String> value;
			if(entry.getValue() == null) {
				value = null;
			} else if(entry.getValue().length == 0) {
				value = Collections.emptyList();
			} else {
				value = Collections.unmodifiableList(Arrays.asList(entry.getValue()));
			}
			map.put(entry.getKey(), value);
		}
		return new ParamMap(map);
	}

	@Override
	public String getFirst(String key) {
		List<String> value = get(key);
		if(value != null && !value.isEmpty()) {
			return value.iterator().next();
		}
		return null;
	}

	@Override
	public boolean add(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> get(Object key) {
		List<String> value = super.get(key);
		if(value == null && key != null) {
			return super.get(key.toString().toLowerCase(Locale.US));
		}
		return value;
	}
}
