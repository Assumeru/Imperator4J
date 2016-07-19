package com.ee.imperator.api;

import java.util.Map;

import org.json.JSONObject;

import com.ee.imperator.user.Member;

@FunctionalInterface
public interface RequestListener {
	void onRequest(Member member, Map<String, ?> input, JSONObject output);
}
