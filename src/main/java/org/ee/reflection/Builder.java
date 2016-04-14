package org.ee.reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class Builder {
	private final Class<?>[] types;
	private final Object[] values;

	public Builder(Class<?>[] types, Object[] values) {
		this.types = types;
		this.values = values;
	}

	public Builder(Collection<Class<?>> types, Collection<Object> values) {
		this(types.toArray(new Class<?>[types.size()]), values.toArray(new Object[values.size()]));
	}

	public <T> T newInstance(Class<T> type) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return type.getConstructor(types).newInstance(values);
	}
}
