package org.ee.web.response;

import java.util.Collection;

import javax.servlet.http.Cookie;

import org.ee.collection.ListMap;
import org.ee.web.Status;

public interface Response {
	default void setContentType(String type) {
		setHeader("Content-Type", type);
	}

	default void setContentType(String type, String subtype) {
		setContentType(type + "/" + subtype);
	}

	default void setContentType(String type, String subtype, String charset) {
		setContentType(type + "/" + subtype + "; charset=" + charset);
	}

	void setStatus(Status status);

	void setHeader(String key, String value);

	void setOutput(Object output);

	void addCookie(Cookie cookie);

	ListMap<String, String> getHeaders();

	Object getOutput();

	Status getStatus();

	Collection<Cookie> getCookies();
}
