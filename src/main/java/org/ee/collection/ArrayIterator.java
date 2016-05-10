package org.ee.collection;

import java.util.Iterator;

public class ArrayIterator<E> implements Iterator<E> {
	protected E[] array;
	protected int index;

	public ArrayIterator(E[] array) {
		this.array = array;
	}

	@Override
	public boolean hasNext() {
		return index < array.length;
	}

	@Override
	public E next() {
		return array[index++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
