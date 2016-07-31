package com.ee.imperator.web.context;

import java.util.function.Function;

public class Variable<V> {
	private final String name;
	private final Function<PageContext, V> defaultValue;

	public Variable(String name, Function<PageContext, V> defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public V getDefaultValue(PageContext context) {
		return defaultValue.apply(context);
	}

	public void setOn(PageContext context, Object value) {
		context.setVariable(getName(), value);
	}
}
