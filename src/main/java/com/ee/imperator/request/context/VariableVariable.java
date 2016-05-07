package com.ee.imperator.request.context;

import java.util.Map;
import java.util.WeakHashMap;

public abstract class VariableVariable<T> extends Variable<T> {
	private Map<PageContext, T> values = new WeakHashMap<>();

	public VariableVariable(String name) {
		super(name, null);
	}

	@Override
	public T getDefaultValue(PageContext context) {
		T value = values.get(context);
		if(value == null) {
			value = newInstance();
			values.put(context, value);
		}
		return value;
	}

	protected abstract T newInstance();
}
