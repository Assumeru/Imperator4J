package com.ee.imperator;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.ee.web.WebApplication;
import org.ee.web.request.AbstractRequestResolver;

import com.ee.imperator.map.MapParser;
import com.ee.imperator.request.RequestResolver;

public class Imperator extends WebApplication {
	private static Map<Integer, com.ee.imperator.map.Map> maps;

	public Imperator(@Context ServletContext context) {
		super(context);
	}

	@Override
	protected Class<? extends AbstractRequestResolver> getRequestResolver() {
		return RequestResolver.class;
	}

	public static Map<Integer, com.ee.imperator.map.Map> getMaps() {
		if(maps == null) {
			loadMaps();
		}
		return maps;
	}

	private synchronized static void loadMaps() {
		if(maps == null) {
			maps = MapParser.parseMaps(getFiles("/WEB-INF/maps/", ".xml"));
		}
	}
}
