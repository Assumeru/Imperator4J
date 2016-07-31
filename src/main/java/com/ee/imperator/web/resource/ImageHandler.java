package com.ee.imperator.web.resource;

import java.util.Map;

import org.ee.collection.MapBuilder;
import org.ee.web.request.Request;
import org.ee.web.request.ResourceHandler;

import com.google.common.io.Files;

public class ImageHandler extends ResourceHandler {
	private static final Map<String, String> TYPES = new MapBuilder<String, String>()
			.put("svg", "application/svg+xml")
			.put("png", "image/png")
			.put("jpg", "image/jpeg")
			.putGet("jpeg", "jpg")
			.build(true);

	@Override
	public boolean matches(Request request) {
		return request.getPath().startsWith("img/");
	}

	@Override
	protected String getType(Request request) {
		String extension = Files.getFileExtension(request.getPath());
		String type = TYPES.get(extension);
		if(type != null) {
			return type;
		}
		return "application/octet-stream";
	}
}
