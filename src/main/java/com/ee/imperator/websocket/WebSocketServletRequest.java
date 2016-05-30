package com.ee.imperator.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.websocket.server.HandshakeRequest;

import com.ee.imperator.Imperator;

public class WebSocketServletRequest implements HttpServletRequest {
	private final Map<String, String[]> parameterMap;
	private final URI requestURI;
	private final String queryString;
	private final Principal userPrincipal;
	private final Cookie[] cookies;
	private final Map<String, List<String>> headers;

	public WebSocketServletRequest(HandshakeRequest request, Map<String, javax.ws.rs.core.Cookie> cookies) {
		parameterMap = new HashMap<>();
		for(Entry<String, List<String>> entry : request.getParameterMap().entrySet()) {
			parameterMap.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
		}
		queryString = request.getQueryString();
		userPrincipal = request.getUserPrincipal();
		requestURI = request.getRequestURI();
		this.cookies = new Cookie[cookies.size()];
		int i = 0;
		for(javax.ws.rs.core.Cookie cookie : cookies.values()) {
			this.cookies[i++] = new Cookie(cookie.getName(), cookie.getValue());
		}
		headers = request.getHeaders();
	}

	@Override
	public Object getAttribute(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.emptyEnumeration();
	}

	@Override
	public String getCharacterEncoding() {
		return "UTF-8";
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
	}

	@Override
	public int getContentLength() {
		return -1;
	}

	@Override
	public long getContentLengthLong() {
		return -1;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getParameter(String name) {
		String[] values = getParameterValues(name);
		if(values != null && values.length > 0) {
			return values[0];
		}
		return null;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(parameterMap.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return parameterMap.get(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}

	@Override
	public String getProtocol() {
		return null;
	}

	@Override
	public String getScheme() {
		return requestURI.getScheme();
	}

	@Override
	public String getServerName() {
		return requestURI.getHost();
	}

	@Override
	public int getServerPort() {
		return requestURI.getPort();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteAddr() {
		return "127.0.0.1";
	}

	@Override
	public String getRemoteHost() {
		return "127.0.0.1";
	}

	@Override
	public void setAttribute(String name, Object o) {
	}

	@Override
	public void removeAttribute(String name) {
	}

	@Override
	public Locale getLocale() {
		return Locale.getDefault();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return Collections.enumeration(Arrays.asList(Locale.getDefault()));
	}

	@Override
	public boolean isSecure() {
		return "https".equalsIgnoreCase(getProtocol());
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRealPath(String path) {
		return Imperator.getContext().getRealPath(path);
	}

	@Override
	public int getRemotePort() {
		return 0;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public String getLocalAddr() {
		return null;
	}

	@Override
	public int getLocalPort() {
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		return Imperator.getContext();
	}

	@Override
	public AsyncContext startAsync() {
		throw new IllegalStateException();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
		throw new IllegalStateException();
	}

	@Override
	public boolean isAsyncStarted() {
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		return false;
	}

	@Override
	public AsyncContext getAsyncContext() {
		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {
		return DispatcherType.REQUEST;
	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		return cookies.length == 0 ? null : cookies;
	}

	@Override
	public long getDateHeader(String name) {
		DateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
		try {
			return format.parse(getHeader(name)).getTime();
		} catch(ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getHeader(String name) {
		List<String> header = headers.get(name);
		if(header != null && !header.isEmpty()) {
			return header.get(0);
		}
		return null;
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		return Collections.enumeration(headers.get(name));
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(headers.keySet());
	}

	@Override
	public int getIntHeader(String name) {
		return Integer.parseInt(getHeader(name));
	}

	@Override
	public String getMethod() {
		return "GET";
	}

	@Override
	public String getPathInfo() {
		return null;
	}

	@Override
	public String getPathTranslated() {
		return null;
	}

	@Override
	public String getContextPath() {
		return Imperator.getContext().getContextPath();
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		return userPrincipal;
	}

	@Override
	public String getRequestedSessionId() {
		return null;
	}

	@Override
	public String getRequestURI() {
		return requestURI.toString();
	}

	@Override
	public StringBuffer getRequestURL() {
		return new StringBuffer(requestURI.toString());
	}

	@Override
	public String getServletPath() {
		return Imperator.getContext().getContextPath();
	}

	@Override
	public HttpSession getSession(boolean create) {
		return null;
	}

	@Override
	public HttpSession getSession() {
		return null;
	}

	@Override
	public String changeSessionId() {
		return null;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		return false;
	}

	@Override
	public void login(String username, String password) throws ServletException {
		throw new ServletException();
	}

	@Override
	public void logout() throws ServletException {
		throw new ServletException();
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		return Collections.emptyList();
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		return null;
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		return null;
	}
}
