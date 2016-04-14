package org.ee.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Collection;

public class Builder {
	private final Class<?>[] types;
	private Object[] values;

	public Builder(Class<?>[] types, Object[] values) {
		if(types.length != values.length) {
			throw new IllegalArgumentException("types.length != values.length");
		}
		this.types = types;
		this.values = values;
	}

	public Builder(Collection<Class<?>> types, Collection<Object> values) {
		this(types.toArray(new Class<?>[types.size()]), values.toArray(new Object[values.size()]));
	}

	public <T> T newInstance(Class<T> type) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		try {
			return newInstance(type.getConstructor(types));
		} catch (NoSuchMethodException e) {
			return newInstance(getConstructor(type));
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Constructor<T> getConstructor(Class<T> type) throws NoSuchMethodException {
		for(Constructor<?> constructor : type.getConstructors()) {
			if(isAppropriateConstructor(constructor)) {
				return (Constructor<T>) constructor;
			}
		}
		throw new NoSuchMethodException("Failed to find constructor matching argument types");
	}

	private boolean isAppropriateConstructor(Constructor<?> constructor) {
		int i = 0;
		for(Parameter param : constructor.getParameters()) {
			if(i >= types.length || param.getType() != types[i]) {
				if(param.isVarArgs()) {
					int end = i;
					while(i < types.length && param.getType().getComponentType() == types[i]) {
						i++;
					}
					if(i == types.length) {
						Object varArgs = Array.newInstance(param.getType().getComponentType(), i - end);
						for(int n = end; n < i; n++) {
							Array.set(varArgs, n - end, values[n]);
						}
						Object[] values = new Object[end + 1];
						System.arraycopy(this.values, 0, values, 0, end);
						values[end] = varArgs;
						this.values = values;
						return true;
					}
				}
				return false;
			}
			i++;
		}
		return true;
	}

	public <T> T newInstance(Constructor<T> constructor) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return constructor.newInstance(values);
	}
}
