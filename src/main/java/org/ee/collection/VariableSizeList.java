package org.ee.collection;

import java.util.Arrays;

/**
 * Array-backed list that doesn't throw ConcurrentModificationExceptions
 */
public class VariableSizeList<T> extends FixedSizeList<T> {
	private static final int DEFAULT_CAPACITY = 5;
	private static final int DEFAULT_GROWTH = 5;
	private int growth;

	public VariableSizeList() {
		this(DEFAULT_CAPACITY);
	}

	public VariableSizeList(int initialCapacity) {
		this(initialCapacity, DEFAULT_GROWTH);
	}

	public VariableSizeList(int initialCapacity, int growth) {
		super(initialCapacity);
		this.growth = growth;
	}

	@Override
	public void add(int index, T element) {
		if(index >= values.length) {
			values = Arrays.copyOf(values, values.length + growth);
		}
		super.add(index, element);
	}

	@Override
	public void clear() {
		values = new Object[DEFAULT_CAPACITY];
		super.clear();
	}
}
