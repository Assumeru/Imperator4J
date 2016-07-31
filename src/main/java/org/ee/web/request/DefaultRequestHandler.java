package org.ee.web.request;

import org.ee.web.Status;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;

public class DefaultRequestHandler implements RequestHandler {
	private final Status status;

	public DefaultRequestHandler() {
		this(Status.NO_CONTENT);
	}

	public DefaultRequestHandler(Status status) {
		this.status = status;
	}

	@Override
	public Response handle(Request request) {
		return new SimpleResponse(status);
	}
}
