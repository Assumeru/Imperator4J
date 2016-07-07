package org.ee.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChangeCollection<E> implements Collection<E> {
	private final Collection<E> parent;
	private final List<E> added;
	private final List<E> removed;

	public ChangeCollection(Collection<E> parent) {
		this.parent = parent;
		added = Collections.synchronizedList(new ArrayList<>());
		removed = Collections.synchronizedList(new ArrayList<>());
	}

	public List<E> getAdded() {
		return added;
	}

	public List<E> getRemoved() {
		return removed;
	}

	@Override
	public int size() {
		return parent.size();
	}

	@Override
	public boolean isEmpty() {
		return parent.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return parent.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private final Iterator<E> it = parent.iterator();
			private E current;

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public E next() {
				return current = it.next();
			}

			@Override
			public void remove() {
				removed.add(current);
				added.remove(current);
				it.remove();
			}
		};
	}

	@Override
	public Object[] toArray() {
		return parent.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return parent.toArray(a);
	}

	@Override
	public boolean add(E e) {
		boolean changed = parent.add(e);
		if(changed) {
			added.add(e);
			removed.remove(e);
		}
		return changed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		boolean changed = parent.remove(o);
		if(changed) {
			removed.add((E) o);
			added.remove(o);
		}
		return changed;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return parent.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return parent.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return parent.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return parent.retainAll(c);
	}

	@Override
	public void clear() {
		removed.addAll(parent);
		added.removeAll(parent);
		parent.clear();
	}
}
