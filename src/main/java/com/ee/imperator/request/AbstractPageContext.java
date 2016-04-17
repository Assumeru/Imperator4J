package com.ee.imperator.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.ee.web.request.page.WebPage;

import com.ee.imperator.game.Game;
import com.ee.imperator.map.HasFlag;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Member;

public abstract class AbstractPageContext implements PageContext {
	private final Member user;
	private final List<WebPage> navigationPages;
	private final String path;

	public AbstractPageContext(Member user, List<WebPage> navigationPages, String path) {
		this.user = user;
		this.navigationPages = navigationPages;
		this.path = path;
	}

	@Override
	public Member getUser() {
		return user;
	}

	@Override
	public List<WebPage> getNavigationPages() {
		return navigationPages;
	}

	@Override
	public String getPath() {
		return path;
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
