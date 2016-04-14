package org.ee.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetBuilder<T> {
	private final Set<T> set;

	public SetBuilder() {
		this(new HashSet<>());
	}

	public SetBuilder(int capacity) {
		this(new HashSet<>(capacity));
	}

	public SetBuilder(Set<T> set) {
		this.set = set;
	}

	public SetBuilder<T> add(T value) {
		set.add(value);
		return this;
	}

	public Set<T> build() {
		return build(false);
	}

	public Set<T> build(boolean immutable) {
		if(immutable) {
			return Collections.unmodifiableSet(set);
		}
		return set;
	}
}
