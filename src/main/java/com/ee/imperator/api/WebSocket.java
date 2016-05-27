package com.ee.imperator.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.ee.imperator.exception.RequestException;
import com.ee.imperator.user.Member;

public class WebSocket implements RequestHandler<JSONObject, String> {
	WebSocket() {
	}

	@Override
	public String handle(JSONObject input) {
		return handle((Member) input.get("member"), input.getJSONObject("input"));
	}

	public String handle(Member member, JSONObject input) {
		try {
			return Api.handleRequest(getVariables(input), member);
		} catch(RequestException e) {
			return new JSONObject().put("type", e.getType()).put("mode", e.getMode()).put("error", e.getMessage(member.getLanguage())).toString();
		}
	}

	private Map<String, String> getVariables(JSONObject input) {
		Map<String, String> out = new HashMap<>();
		for(String key : input.keySet()) {
			out.put(key, String.valueOf(input.get(key)));
		}
		return out;
	}
}
