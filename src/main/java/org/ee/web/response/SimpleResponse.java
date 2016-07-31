package org.ee.web.response;

import org.ee.collection.ListHashMap;
import org.ee.collection.ListMap;
import org.ee.web.Status;

public class SimpleResponse implements Response {
	private final ListMap<String, String> headers = new ListHashMap<>();
	private Status status;
	private Object output;

	public SimpleResponse() {
		this(Status.NO_CONTENT, null);
	}

	public SimpleResponse(Status status) {
		this(status, null);
	}

	public SimpleResponse(Status status, Object output) {
		this.status = status;
		this.output = output;
	}

	@Override
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void setHeader(String key, String value) {
		headers.add(key, value);
	}

	@Override
	public void setOutput(Object output) {
		this.output = output;
	}

	@Override
	public ListMap<String, String> getHeaders() {
		return headers;
	}

	@Override
	public Object getOutput() {
		return output;
	}
}
