package com.ee.imperator.api;

import java.util.Map;

import org.json.JSONObject;

import com.ee.imperator.user.Member;

/**
 * Provides a way of detecting and modifying API calls.
 */
@FunctionalInterface
public interface RequestListener {
	/**
	 * Called after a request has been handled.
	 * May change the output.
	 * 
	 * @param member The user who made the request
	 * @param input The variables that make up the request
	 * @param output The reply to be sent to the user (can be null)
	 */
	void onRequest(Member member, Map<String, ?> input, JSONObject output);
}
