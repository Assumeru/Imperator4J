package org.ee.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ReflectionUtils {
	private ReflectionUtils() {
	}

	public static List<Method> getMethodsUntil(Class<?> type, Class<?> parentType) {
		List<Method> methods = new ArrayList<>();
		Class<?> current = type;
		while(current != parentType && current != null) {
			methods.addAll(Arrays.asList(current.getMethods()));
			current = current.getSuperclass();
		}
		return methods;
	}

	public static <T> Class<T> getSubclass(String className, Class<T> parentClass) throws ClassNotFoundException {
		return getSubclass(Class.forName(className), parentClass);
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getSubclass(Class<?> type, Class<T> parentClass) throws ClassNotFoundException {
		if(parentClass.isAssignableFrom(type)) {
			return (Class<T>) type;
		}
		throw new IllegalArgumentException(type + " is not a subclass of " + parentClass);
	}
}
