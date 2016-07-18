package com.ee.imperator.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.ee.imperator.api.handlers.Param;
import com.ee.imperator.user.Member;

class Handler implements Comparable<Handler> {
	private final List<String> names;
	private final Object target;
	private final Method method;

	public Handler(Object target, Method method) {
		Parameter[] params = method.getParameters();
		this.names = new ArrayList<>(params.length - 1);
		for(int i = 1; i < params.length; i++) {
			names.add(params[i].getAnnotation(Param.class).value());
		}
		this.method = method;
		this.target = target;
	}

	public Match getMatch(Map<String, ?> variables, Member member) {
		if(variables.keySet().containsAll(names)) {
			Class<?>[] types = method.getParameterTypes();
			Match match = new Match(member);
			for(int i = 0; i < names.size(); i++) {
				if(!match.add(types[i + 1], variables.get(names.get(i)))) {
					return null;
				}
			}
			return match;
		}
		return null;
	}

	public JSONObject invoke(Match match) throws IllegalAccessException, InvocationTargetException {
		return (JSONObject) method.invoke(target, match.getArguments());
	}

	@Override
	public int compareTo(Handler o) {
		return Integer.compare(o.names.size(), names.size());
	}
}
