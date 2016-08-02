package com.ee.imperator.url;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.HasFlag;
import com.ee.imperator.map.Map;

public class DefaultUrlBuilder implements UrlBuilder {
	private final String basepath;
	private final String contextPath;

	public DefaultUrlBuilder(ImperatorApplicationContext context) {
		basepath = context.getConfig().getString(getClass(), "basepath");
		String contextPath = context.getContextPath();
		if(contextPath.endsWith("/")) {
			this.contextPath = contextPath.substring(0, contextPath.length() - 1);
		} else {
			this.contextPath = contextPath;
		}
	}

	@Override
	public String buildLink(String url) {
		return basepath + contextPath + url;
	}

	@Override
	public String css(String file) {
		return "/css/" + file;
	}

	@Override
	public String javascript(String file) {
		return "/js/" + file;
	}

	@Override
	public String image(String file) {
		return "/img/" + file;
	}

	@Override
	public String flag(HasFlag location) {
		return image(location.getPath());
	}

	@Override
	public String map(Map map) {
		return "/map/" + getNamedUrlBit(map.getId(), map.getName());
	}

	@Override
	public String game(Game game) {
		return "/game/" + getNamedUrlBit(game.getId(), game.getName());
	}

	private String getNamedUrlBit(int id, String name) {
		try {
			return id + "/" + URLEncoder.encode(name, "UTF-8").replace("%2F", "/").replace("%2f", "/");
		} catch(UnsupportedEncodingException e) {
			return id + "/";
		}
	}
}
