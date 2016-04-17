package com.ee.imperator.request;

import java.util.List;

import org.ee.web.request.page.WebPage;

import com.ee.imperator.map.Map;
import com.ee.imperator.map.Region;
import com.ee.imperator.map.Territory;
import com.ee.imperator.request.page.PageContext;
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
	public String map(Map map) {
		return "/map/" + map.getId() + "/" + map.getName();
	}

	@Override
	public String regionFlag(Region region) {
		return image("flags/" + region.getId().replaceAll("_", "/") + ".png");
	}

	@Override
	public String territoryFlag(Territory territory) {
		return image("flags/" + territory.getId().replaceAll("_", "/") + ".png");
	}
}
