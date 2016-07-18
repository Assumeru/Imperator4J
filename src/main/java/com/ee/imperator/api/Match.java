package com.ee.imperator.api;

import java.util.ArrayList;
import java.util.List;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.text.PrimitiveUtils;

import com.ee.imperator.user.Member;

class Match {
	private static final Logger LOG = LogManager.createLogger();
	private List<Object> arguments;

	public Match(Member member) {
		arguments = new ArrayList<>();
		arguments.add(member);
	}

	public boolean add(Class<?> type, Object value) {
		if(type.isAssignableFrom(value.getClass()) || (type.isPrimitive() && PrimitiveUtils.equals(type, value.getClass()))) {
			return arguments.add(value);
		}
		return addString(type, String.valueOf(value));
	}

	private boolean addString(Class<?> type, String value) {
		if(PrimitiveUtils.isPrimitive(type)) {
			try {
				return arguments.add(PrimitiveUtils.parse(type, value));
			} catch(IllegalArgumentException e) {
				LOG.v("Argument " + value + " was not of expected type " + type, e);
			}
		} else if(type.isAssignableFrom(String.class)) {
			return arguments.add(value);
		}
		return false;
	}

	public Object[] getArguments() {
		return arguments.toArray();
	}
}
