package com.ee.imperator.request.page;

import java.util.function.Function;

import com.ee.imperator.request.PageContext;

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
}
