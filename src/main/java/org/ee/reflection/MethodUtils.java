package org.ee.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MethodUtils {
	private MethodUtils() {
	}

	public static List<Method> getMethodsUntil(Class<?> type, Class<?> parentType) {
		List<Method> methods = new ArrayList<>();
		Class<?> current = type;
		while(current != parentType && current != null) {
			methods.addAll(Arrays.asList(current.getMethods()));
			current = type.getSuperclass();
		}
		return methods;
	}
}
