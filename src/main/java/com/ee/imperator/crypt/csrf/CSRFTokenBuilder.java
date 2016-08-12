package com.ee.imperator.crypt.csrf;

import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.imperator.web.context.PageContext;

public interface CSRFTokenBuilder {
	boolean shouldSetToken(Request request);

	void setToken(Request request, Response response);

	boolean tokenIsValid(Request request);

	void addToPage(PageContext context);
}
