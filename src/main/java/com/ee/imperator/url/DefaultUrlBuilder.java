package com.ee.imperator.url;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ee.imperator.Imperator;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.HasFlag;
import com.ee.imperator.map.Map;

public class DefaultUrlBuilder {
	private String contextPath;

	public DefaultUrlBuilder() {
		String contextPath = Imperator.getContextPath();
		if(contextPath.endsWith("/")) {
			this.contextPath = contextPath;
		} else {
			this.contextPath = contextPath + "/";
		}
	}

	public String buildLink(String url) {
		return contextPath + url;
	}

	public String css(String file) {
		return "/css/" + file;
	}

	public String javascript(String file) {
		return "/js/" + file;
	}

	public String image(String file) {
		return "/img/" + file;
	}

	public String flag(HasFlag location) {
		return image(location.getPath());
	}

	public String map(Map map) {
		return "/map/" + getNamedUrlBit(map.getId(), map.getName());
	}

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
