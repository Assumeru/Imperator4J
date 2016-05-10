package org.ee.collection;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestVariableSizeList {
	@Test
	public void test() {
		List<Integer> list = new VariableSizeList<>(10, 50);
		for(int i = 0; i < 1000; i++) {
			list.add(i);
		}
		Assert.assertEquals(1000, list.size());
		for(Integer i : list) {
			list.remove(i);
		}
		Assert.assertTrue(list.isEmpty());
		list.add(2);
		list.clear();
		Assert.assertTrue(list.isEmpty());
	}
}
