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

	public boolean add(Class<?> type, String value) {
		if(type.isAssignableFrom(String.class)) {
			return arguments.add(value);
		} else if(PrimitiveUtils.isPrimitive(type)) {
			try {
				return arguments.add(PrimitiveUtils.parse(type, value));
			} catch(IllegalArgumentException e) {
				LOG.v("Argument " + value + " was not of expected type " + type, e);
			}
		}
		return false;
	}

	public Object[] getArguments() {
		return arguments.toArray();
	}
}
