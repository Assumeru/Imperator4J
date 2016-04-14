package com.ee.imperator.request.resource;

import javax.ws.rs.core.MediaType;

import org.ee.web.request.Request;
import org.ee.web.request.ResourceHandler;

public class JavaScriptHandler extends ResourceHandler {
	private static final MediaType JAVASCRIPT = new MediaType("application", "javascript");

	@Override
	public boolean matches(String path) {
		return path.startsWith("js/");
	}

	@Override
	protected MediaType getType(Request request) {
		return JAVASCRIPT;
	}
}
