package org.ee.web.exception;

import org.ee.web.Status;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;

public class WebException extends RuntimeException {
	private static final long serialVersionUID = -2052312822147670827L;
	private final transient Response response;

	public WebException() {
		this(Status.INTERNAL_SERVER_ERROR);
	}

	public WebException(Status status) {
		this(new SimpleResponse(status));
	}

	public WebException(Throwable cause) {
		this(new SimpleResponse(Status.INTERNAL_SERVER_ERROR), cause);
	}

	public WebException(Response response) {
		super();
		this.response = response;
	}

	public WebException(Response response, Throwable cause) {
		super(cause);
		this.response = response;
	}

	public Response getResponse() {
		return response;
	}
}
