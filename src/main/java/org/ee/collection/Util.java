package org.ee.collection;

import java.lang.reflect.Array;

public class Util {
	private Util() {
	}

	public static void shuffle(Object array) {
		if(!array.getClass().isArray()) {
			throw new IllegalArgumentException("Input is not an array");
		}
		int length = Array.getLength(array);
		for(int i = 0; i < length; i++) {
			int r = (int) (Math.random() * length);
			Object o = Array.get(array, i);
			Array.set(array, i, Array.get(array, r));
			Array.set(array, r, o);
		}
	}
}
