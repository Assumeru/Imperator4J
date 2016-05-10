package org.ee.collection;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.RandomAccess;

/**
 * Fixed-size, array-backed list that doesn't throw ConcurrentModificationExceptions
 */
public class FixedSizeList<T> extends AbstractList<T> implements RandomAccess {
	protected Object[] values;
	private int size;

	public FixedSizeList(int size) {
		values = new Object[size];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		if(index >= size) {
			throw new IndexOutOfBoundsException();
		}
		return (T) values[index];
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void add(int index, T element) {
		if(index >= values.length) {
			throw new IllegalArgumentException("list is full");
		} else if(index > size) {
			throw new IndexOutOfBoundsException();
		} else if(index == size) {
			values[size++] = element;
		} else {
			System.arraycopy(values, index, values, index + 1, values.length - index - 1);
			values[index] = element;
			size++;
		}
	}

	@Override
	public boolean remove(Object o) {
		for(int i = 0; i < size; i++) {
			if(o == values[i] || (values[i] != null && values[i].equals(o))) {
				remove(i);
			}
		}
		return false;
	}

	@Override
	public T remove(int index) {
		T elem = get(index);
		if(index == size - 1) {
			values[index] = null;
			size--;
		} else {
			System.arraycopy(values, index + 1, values, index, values.length - index - 1);
			size--;
		}
		return elem;
	}

	@Override
	public T set(int index, T element) {
		T old = get(index);
		values[index] = element;
		return old;
	}

	@Override
	public void clear() {
		size = 0;
		for(int i = 0; i < values.length; i++) {
			values[i] = null;
		}
	}

	@Override
	public Object[] toArray() {
		return Arrays.copyOf(values, size);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sort(Comparator<? super T> c) {
		Arrays.sort((T[]) values, 0, size, c);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator<>((T[]) toArray());
	}
}
