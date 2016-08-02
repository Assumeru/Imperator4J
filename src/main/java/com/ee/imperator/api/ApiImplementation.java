package com.ee.imperator.api;

import java.util.Map;

import org.json.JSONObject;

import com.ee.imperator.exception.RequestException;
import com.ee.imperator.user.Member;

class ApiImplementation {
	protected final Api api;

	protected ApiImplementation(Api api) {
		this.api = api;
	}

	protected JSONObject handleRequest(Map<String, ?> variables, Member member) throws RequestException {
		return api.handleRequest(variables, member);
	}
}
