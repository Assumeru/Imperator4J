package org.ee.web.response;

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

	Status getStatus();

	void setHeader(String key, String value);

	void setOutput(Object output);

	ListMap<String, String> getHeaders();

	Object getOutput();
}
