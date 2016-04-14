package org.ee.text;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestPrimitiveUtils {
	@Test
	public void testIsPrimitive() {
		for(Class<?> c : new Class<?>[] { boolean.class, Boolean.class, byte.class, Byte.class, char.class, Character.class, double.class, Double.class, float.class, Float.class, short.class, Short.class, long.class, Long.class, int.class, Integer.class }) {
			assertTrue(PrimitiveUtils.isPrimitive(c));
		}
		assertFalse(PrimitiveUtils.isPrimitive(Object.class));
		assertFalse(PrimitiveUtils.isPrimitive(String.class));
		assertFalse(PrimitiveUtils.isPrimitive(new Object()));
		assertFalse(PrimitiveUtils.isPrimitive(""));
	}

	@Test
	public void testGetClass() {
		assertEquals(boolean.class, PrimitiveUtils.getClass("boolean"));
		assertEquals(byte.class, PrimitiveUtils.getClass("byte"));
		assertEquals(char.class, PrimitiveUtils.getClass("char"));
		assertEquals(double.class, PrimitiveUtils.getClass("double"));
		assertEquals(float.class, PrimitiveUtils.getClass("float"));
		assertEquals(short.class, PrimitiveUtils.getClass("short"));
		assertEquals(long.class, PrimitiveUtils.getClass("long"));
		assertEquals(int.class, PrimitiveUtils.getClass("int"));
		try {
			PrimitiveUtils.getClass("java.lang.Object");
			assertEquals(IllegalArgumentException.class, null);
		} catch(IllegalArgumentException e) {
		}
	}

	@Test
	public void testParse() {
		assertTrue(1234L == PrimitiveUtils.parse(long.class, "1234"));
		assertTrue(1234 == PrimitiveUtils.parse(Integer.class, "1234"));
	}
}
