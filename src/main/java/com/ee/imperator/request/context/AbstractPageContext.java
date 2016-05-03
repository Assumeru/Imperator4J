package com.ee.imperator.request.context;

import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import org.ee.web.request.Request;
import org.ee.web.request.page.WebPage;

import com.ee.imperator.user.Member;

public abstract class AbstractPageContext implements PageContext {
	private final Member user;
	private final List<WebPage> navigationPages;
	private final Request request;

	public AbstractPageContext(Member user, List<WebPage> navigationPages, Request request) {
		this.user = user;
		this.navigationPages = navigationPages;
		this.request = request;
	}

	@Override
	public Member getUser() {
		return user;
	}

	@Override
	public java.util.Map<String, Cookie> getCookies() {
		return request.getCookies();
	}

	@Override
	public MultivaluedMap<String, String> getGetParams() {
		return request.getGetParams();
	}

	@Override
	public MultivaluedMap<String, String> getPostParams() {
		return request.getPostParams();
	}

	@Override
	public List<WebPage> getNavigationPages() {
		return navigationPages;
	}

	@Override
	public String getPath() {
		return request.getPath();
	}
}
