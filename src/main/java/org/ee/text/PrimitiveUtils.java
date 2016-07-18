package org.ee.text;

import java.util.Map;
import java.util.Set;

import org.ee.collection.MapBuilder;
import org.ee.collection.SetBuilder;

public final class PrimitiveUtils {
	private static final Set<Class<?>> PRIMITIVES = new SetBuilder<Class<?>>()
			.add(Boolean.class)
			.add(Byte.class)
			.add(Character.class)
			.add(Double.class)
			.add(Float.class)
			.add(Long.class)
			.add(Short.class)
			.add(Integer.class)
			.add(Void.class)
			.build(true);
	private static final Map<String, Class<?>> PRIMITIVE_NAMES = new MapBuilder<String, Class<?>>()
			.put("boolean", boolean.class)
			.put("byte", byte.class)
			.put("char", char.class)
			.put("double", double.class)
			.put("float", float.class)
			.put("long", long.class)
			.put("short", short.class)
			.put("int", int.class)
			.put("void", void.class)
			.build(true);
	private static final Map<Class<?>, Class<?>> BOXES = new MapBuilder<Class<?>, Class<?>>()
			.put(boolean.class, Boolean.class)
			.put(byte.class, Byte.class)
			.put(char.class, Character.class)
			.put(double.class, Double.class)
			.put(float.class, Float.class)
			.put(long.class, Long.class)
			.put(short.class, Short.class)
			.put(int.class, Integer.class)
			.put(void.class, Void.class)
			.build(true);

	private PrimitiveUtils() {
	}

	public static boolean isPrimitive(Class<?> type) {
		return type.isPrimitive() || PRIMITIVES.contains(type);
	}

	public static boolean isPrimitive(Object o) {
		return o != null && PRIMITIVES.contains(o.getClass());
	}

	public static Class<?> getClass(String type) {
		if(PRIMITIVE_NAMES.containsKey(type)) {
			return PRIMITIVE_NAMES.get(type);
		}
		throw new IllegalArgumentException(type + " is not a primitive");
	}

	@SuppressWarnings("unchecked")
	public static <E> E parse(Class<E> type, String input) {
		if(!isPrimitive(type)) {
			throw new IllegalArgumentException(type + " is not a primitive type");
		}
		if(input == null) {
			throw new NullPointerException("input == null");
		}
		if(type == boolean.class || type == Boolean.class) {
			return (E) Boolean.valueOf(Boolean.parseBoolean(input));
		} else if(type == byte.class || type == Byte.class) {
			return (E) Byte.valueOf(Byte.parseByte(input));
		} else if(type == char.class || type == Character.class) {
			if(input.length() > 1) {
				throw new IllegalArgumentException("input is more than 1 character");
			}
			return (E) Character.valueOf(input.charAt(0));
		} else if(type == double.class || type == Double.class) {
			return (E) Double.valueOf(Double.parseDouble(input));
		} else if(type == float.class || type == Float.class) {
			return (E) Float.valueOf(Float.parseFloat(input));
		} else if(type == short.class || type == Short.class) {
			return (E) Short.valueOf(Short.parseShort(input));
		} else if(type == long.class || type == Long.class) {
			return (E) Long.valueOf(Long.parseLong(input));
		} else if(type == int.class || type == Integer.class) {
			return (E) Integer.valueOf(Integer.parseInt(input));
		}
		return null;
	}

	public static boolean equals(Class<?> type1, Class<?> type2) {
		if(type1 == type2) {
			return true;
		} else if(isPrimitive(type1) && isPrimitive(type2)) {
			if(type1.isPrimitive()) {
				return type2 == BOXES.get(type1);
			}
			return type1 == BOXES.get(type2);
		}
		return false;
	}
}
