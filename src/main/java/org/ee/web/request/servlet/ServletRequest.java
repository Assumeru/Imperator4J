package org.ee.web.request.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ee.collection.ListHashMap;
import org.ee.collection.ListMap;
import org.ee.web.request.ParamMap;
import org.ee.web.request.Request;
import org.ee.web.request.RequestContext;

public class ServletRequest implements Request {
	private final ListMap<String, String> headers;
	private final ListMap<String, String> getParameters;
	private final ListMap<String, String> postParameters;
	private final String path;
	private final String fullPath;
	private final Method method;
	private final RequestContext context;
	private final Map<String, Cookie> cookies;
	private final Locale locale;

	public ServletRequest(HttpServletRequest request, HttpServletResponse response) {
		headers = initHeaders(request);
		getParameters = ParamMap.of(request.getParameterMap());
		path = request.getPathInfo() == null ? "" : request.getPathInfo().substring(1);
		fullPath = request.getRequestURI();
		method = Method.fromString(request.getMethod());
		if(method == Method.POST) {
			postParameters = getParameters;
		} else {
			postParameters = ParamMap.INSTANCE;
		}
		cookies = initCookies(request.getCookies());
		context = new ServletRequestContext(request.getServletContext(), request, response);
		locale = request.getLocale();
	}

	private ParamMap initHeaders(HttpServletRequest request) {
		ListMap<String, String> out = new ListHashMap<>();
		Enumeration<String> names = request.getHeaderNames();
		while(names.hasMoreElements()) {
			String header = names.nextElement();
			Enumeration<String> values = request.getHeaders(header);
			while(values.hasMoreElements()) {
				out.add(header, values.nextElement());
			}
		}
		return new ParamMap(out);
	}

	private Map<String, Cookie> initCookies(Cookie[] cookies) {
		if(cookies == null) {
			return Collections.emptyMap();
		}
		Map<String, Cookie> out = new HashMap<>();
		for(Cookie cookie : cookies) {
			out.put(cookie.getName(), cookie);
		}
		return Collections.unmodifiableMap(out);
	}

	@Override
	public ListMap<String, String> getGetParameters() {
		return getParameters;
	}

	@Override
	public ListMap<String, String> getPostParameters() {
		return postParameters;
	}

	@Override
	public String getPath() {
		return path;
	}

	public String getFullPath() {
		return fullPath;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public RequestContext getContext() {
		return context;
	}

	@Override
	public Map<String, Cookie> getCookies() {
		return cookies;
	}

	@Override
	public ListMap<String, String> getHeaders() {
		return headers;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}
}
