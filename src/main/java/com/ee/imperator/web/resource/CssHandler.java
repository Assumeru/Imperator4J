package com.ee.imperator.web.resource;

import org.ee.web.request.Request;
import org.ee.web.request.ResourceHandler;

public class CssHandler extends ResourceHandler {
	@Override
	public boolean matches(Request request) {
		return request.getPath().startsWith("css/");
	}

	@Override
	protected String getType(Request request) {
		return "text/css";
	}
}
