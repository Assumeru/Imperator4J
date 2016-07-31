package com.ee.imperator.api;

import java.util.Map;

import org.json.JSONObject;

import com.ee.imperator.exception.RequestException;
import com.ee.imperator.user.Member;

public class ApiImplementation {
	protected final Api api;

	public ApiImplementation(Api api) {
		this.api = api;
	}

	protected JSONObject handleRequest(Map<String, ?> variables, Member member) throws RequestException {
		return api.handleRequest(variables, member);
	}
}
