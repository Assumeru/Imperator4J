package org.ee.collection;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestFixedSizeList {
	@Test
	public void test() {
		String[] values = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
		List<String> list = new FixedSizeList<>(10);
		for(int i = 0; i < 10; i++) {
			Assert.assertEquals(list.size(), i);
			list.add(values[i]);
		}
		Assert.assertArrayEquals(values, list.toArray());
		list.sort(null);
		Arrays.sort(values);
		Assert.assertArrayEquals(values, list.toArray());
		for(String v : list) {
			list.remove(v);
		}
		Assert.assertTrue(list.isEmpty());
	}
}
