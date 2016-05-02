package com.ee.imperator.request.context;

import java.util.function.Function;

public class Variable<Value> {
	private final String name;
	private final Function<PageContext, Value> defaultValue;

	public Variable(String name, Function<PageContext, Value> defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public Value getDefaultValue(PageContext context) {
		return defaultValue.apply(context);
	}

	public void setOn(PageContext context, Object value) {
		context.setVariable(getName(), value);
	}
}
