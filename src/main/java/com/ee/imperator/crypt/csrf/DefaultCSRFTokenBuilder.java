package com.ee.imperator.crypt.csrf;

import javax.servlet.http.Cookie;

import org.ee.collection.MapBuilder;
import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.crypt.PasswordHasher;
import com.ee.imperator.web.context.PageContext;

public class DefaultCSRFTokenBuilder implements CSRFTokenBuilder {
	private final String cookieName;
	private final int expiry;
	private final String headerName;
	private final PasswordHasher hasher;

	public DefaultCSRFTokenBuilder(ImperatorApplicationContext context) {
		hasher = context.getHasher();
		cookieName = context.getStringSetting(getClass(), "cookie");
		expiry = context.getIntSetting(getClass(), "maxCookieAge");
		headerName = context.getStringSetting(getClass(), "header");
	}

	@Override
	public boolean shouldSetToken(Request request) {
		return !request.getCookies().containsKey(cookieName);
	}

	@Override
	public void setToken(Request request, Response response) {
		Cookie cookie = new Cookie(cookieName, getToken(request));
		cookie.setMaxAge(expiry);
		response.addCookie(cookie);
	}

	private String getToken(Request request) {
		return hasher.hash(String.valueOf(request.hashCode()));
	}

	@Override
	public boolean tokenIsValid(Request request) {
		Cookie cookie = request.getCookies().get(cookieName);
		if(cookie != null && cookie.getValue() != null) {
			return cookie.getValue().equals(request.getHeaders().getFirst(headerName));
		}
		return false;
	}

	@Override
	public void addToPage(PageContext context) {
		PageContext.VARIABLE_JAVASCRIPT.add(context, "csrf.js");
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "CSRF", new MapBuilder<>()
				.put("cookie", cookieName)
				.put("header", headerName).build());
	}
}
