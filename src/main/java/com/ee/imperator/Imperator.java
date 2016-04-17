package com.ee.imperator;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.ee.web.WebApplication;
import org.ee.web.request.AbstractRequestResolver;
import org.ee.web.request.Request;

import com.ee.imperator.cache.GameCache;
import com.ee.imperator.cache.MemberCache;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.MapParser;
import com.ee.imperator.request.RequestResolver;
import com.ee.imperator.user.Member;

public class Imperator extends WebApplication {
	private static Map<Integer, com.ee.imperator.map.Map> maps;
	private static MemberCache memberCache;
	private static GameCache gameCache;

	public Imperator(@Context ServletContext context) {
		super(context);
		memberCache = new MemberCache(60 * 60 * 1000);
		gameCache = new GameCache(0);
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

	public static Member getMember(Request request) {
		return memberCache.getMember(request);
	}

	public static Member getMember(int id) {
		return memberCache.getMember(id);
	}

	public static Game getGame(int id) {
		return gameCache.getGame(id);
	}
}
