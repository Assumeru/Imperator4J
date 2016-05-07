package com.ee.imperator.request.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListVariable<T> extends VariableVariable<List<T>> {
	public ListVariable(String name) {
		super(name);
	}

	@Override
	protected List<T> newInstance() {
		return new ArrayList<>(0);
	}

	public boolean add(PageContext context, T value) {
		return getDefaultValue(context).add(value);
	}

	public boolean addAll(PageContext context, Collection<? extends T> values) {
		return getDefaultValue(context).addAll(values);
	}

    @SafeVarargs
	public final boolean addAll(PageContext context, T... values) {
		return addAll(context, Arrays.asList(values));
	}
}
