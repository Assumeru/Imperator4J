package com.ee.imperator.request.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.ee.web.request.Request;
import org.ee.web.request.ResourceHandler;

import com.google.common.io.Files;

public class FontHandler extends ResourceHandler {
	private static final Map<String, MediaType> TYPES = new HashMap<>();
	static {
		TYPES.put("svg", MediaType.APPLICATION_SVG_XML_TYPE);
		TYPES.put("eot", new MediaType("application", "vnd.ms-fontobject"));
		TYPES.put("ttf", new MediaType("application", "x-font-ttf"));
		TYPES.put("woff", new MediaType("application", "font-woff"));
		TYPES.put("woff2", new MediaType("font", "woff2"));
	}

	@Override
	public boolean matches(String path) {
		return path.startsWith("fonts/");
	}

	@Override
	protected MediaType getType(Request request) {
		String extension = Files.getFileExtension(request.getPath());
		MediaType type = TYPES.get(extension);
		if(type != null) {
			return type;
		}
		return MediaType.APPLICATION_OCTET_STREAM_TYPE;
	}
}
