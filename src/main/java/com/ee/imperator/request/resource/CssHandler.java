package com.ee.imperator.request.resource;

import javax.ws.rs.core.MediaType;

import org.ee.web.request.Request;
import org.ee.web.request.ResourceHandler;

public class CssHandler extends ResourceHandler {
	private static final MediaType CSS = new MediaType("text", "css");

	@Override
	public boolean matches(String path) {
		return path.startsWith("css/");
	}

	@Override
	protected MediaType getType(Request request) {
		return CSS;
	}
}
