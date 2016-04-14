package org.ee.web.request;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class Request {
	private final ServletContext servletContext;
	private final HttpServletRequest request;
	private final String path;
	private Object context;

	public Request(ServletContext servletContext, HttpServletRequest request, String path) {
		this.servletContext = servletContext;
		this.request = request;
		this.path = path;
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

	void setContext(Object context) {
		this.context = context;
	}
}
