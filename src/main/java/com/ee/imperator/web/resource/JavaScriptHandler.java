package com.ee.imperator.web.resource;

import org.ee.web.request.Request;
import org.ee.web.request.ResourceHandler;

public class JavaScriptHandler extends ResourceHandler {
	@Override
	public boolean matches(Request request) {
		return request.getPath().startsWith("js/");
	}

	@Override
	protected String getType(Request request) {
		return "application/javascript";
	}
}
