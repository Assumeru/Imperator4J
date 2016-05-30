package org.ee.cache;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SoftReferenceCache<K, V> implements Map<K, V> {
	private final Map<K, Value<V>> cache;
	private final long timeToKeep;

	public SoftReferenceCache(long timeToKeep) {
		this.timeToKeep = timeToKeep;
		cache = new HashMap<>();
	}

	@Override
	public V get(Object key) {
		Value<V> value = cache.get(key);
		if(value != null) {
			V val = value.getValue();
			if(val == null || shouldBeCleared(value)) {
				cache.remove(key);
				return null;
			}
			return val;
		}
		return null;
	}

	private boolean shouldBeCleared(Value<V> value) {
		return timeToKeep > 0 && System.currentTimeMillis() - value.getCreated() > timeToKeep;
	}

	@Override
	public V put(K key, V value) {
		V previous = get(key);
		cache.put(key, new Value<>(value));
		return previous;
	}

	@Override
	public V remove(Object key) {
		V value = get(key);
		cache.remove(key);
		return value;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public boolean isEmpty() {
		return cache.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return cache.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return cache.containsValue(new Value<>(value));
	}

	@Override
	public Set<K> keySet() {
		return Collections.emptySet();
	}

	@Override
	public Collection<V> values() {
		return Collections.emptyList();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return Collections.emptySet();
	}

	private static class Value<T> {
		private final SoftReference<T> value;
		private final long created;

		public Value(T value) {
			this.value = new SoftReference<>(value);
			created = System.currentTimeMillis();
		}

		public long getCreated() {
			return created;
		}

		public T getValue() {
			return value.get();
		}

		@Override
		public int hashCode() {
			return 7 * Objects.hashCode(getValue());
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			} else if(obj instanceof Value) {
				return Objects.equals(getValue(), ((Value<?>) obj).getValue());
			}
			return false;
		}
	}
}
