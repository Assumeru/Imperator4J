package org.ee.web.request;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.ee.collection.ListMap;

public interface Request {
	enum Method {
		GET, POST, PUT, DELETE, CONNECT, HEAD, OPTIONS, PATCH, TRACE;

		public static Method fromString(String method) {
			for(Method m : values()) {
				if(m.name().equalsIgnoreCase(method)) {
					return m;
				}
			}
			throw new IllegalArgumentException("Unknown method: " + method);
		}
	}

	ListMap<String, String> getGetParameters();

	ListMap<String, String> getPostParameters();

	ListMap<String, String> getHeaders();

	Map<String, Cookie> getCookies();

	String getPath();

	String getFullPath();

	Method getMethod();

	RequestContext getContext();

	Locale getLocale();
}
