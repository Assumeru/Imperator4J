package org.ee.web.request;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

public class Request {
	private final ServletContext servletContext;
	private final HttpServletRequest request;
	private final String path;
	private final Map<String, Cookie> cookies;
	private final MultivaluedMap<String, String> getParams;
	private final MultivaluedMap<String, String> postParams;
	private Object context;

	public Request(ServletContext servletContext, HttpServletRequest request, Map<String, Cookie> cookies, MultivaluedMap<String, String> getParams, MultivaluedMap<String, String> postParams, String path) {
		this.servletContext = servletContext;
		this.request = request;
		this.cookies = cookies;
		this.getParams = getParams;
		this.postParams = postParams;
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

	public Map<String, Cookie> getCookies() {
		return cookies;
	}

	public MultivaluedMap<String, String> getGetParams() {
		return getParams;
	}

	public MultivaluedMap<String, String> getPostParams() {
		return postParams;
	}
}
