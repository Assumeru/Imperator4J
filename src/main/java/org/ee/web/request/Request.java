package org.ee.web.request;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class Request {
	private final ServletContext servletContext;
	private final HttpServletRequest request;
	private final String path;
	private final Object context;

	public Request(ServletContext servletContext, HttpServletRequest request, String path, Object context) {
		this.servletContext = servletContext;
		this.request = request;
		this.path = path;
		this.context = context;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getPath() {
		return path;
	}

	public Object getContext() {
		return context;
	}
}
