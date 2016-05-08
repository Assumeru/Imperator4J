package com.ee.imperator.request.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import org.ee.web.request.Request;
import org.ee.web.request.page.WebPage;

import com.ee.imperator.template.Template;
import com.ee.imperator.user.Member;

public class DefaultPageContext implements PageContext {
	private final Template template;
	private final Member user;
	private final List<WebPage> navigationPages;
	private final Request request;

	public DefaultPageContext(Template template, Member user, List<WebPage> navigationPages, Request request) {
		this.template = template;
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

	@Override
	public ByteArrayOutputStream processPage() {
		final ByteArrayOutputStream response = new ByteArrayOutputStream();
		try {
			template.process(response);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return response;
	}

	@Override
	public void setVariable(String key, Object value) {
		template.setVariable(key, value);
	}
}
