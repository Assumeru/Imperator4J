package com.ee.imperator.request.resource;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.ee.collection.MapBuilder;
import org.ee.web.request.Request;
import org.ee.web.request.ResourceHandler;

import com.google.common.io.Files;

public class ImageHandler extends ResourceHandler {
	private static final Map<String, MediaType> TYPES = new MapBuilder<String, MediaType>()
			.put("svg", MediaType.APPLICATION_SVG_XML_TYPE)
			.put("png", new MediaType("image", "png"))
			.put("jpg", new MediaType("image", "jpeg"))
			.putGet("jpeg", "jpg")
			.build(true);

	@Override
	public boolean matches(String path) {
		return path.startsWith("img/");
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
