package org.ee.web.request.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ee.web.AbstractApplicationContext;
import org.ee.web.request.RequestContext;

public class ServletRequestContext extends AbstractApplicationContext implements RequestContext {
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public ServletRequestContext(javax.servlet.ServletContext context, HttpServletRequest request, HttpServletResponse response) {
		super(context);
		this.request = request;
		this.response = response;
	}

	@Override
	public HttpServletRequest getServletRequest() {
		return request;
	}

	@Override
	public HttpServletResponse getServletResponse() {
		return response;
	}
}
