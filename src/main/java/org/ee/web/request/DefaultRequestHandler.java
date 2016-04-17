package org.ee.web.request;

import javax.ws.rs.core.Response;

import org.ee.web.request.page.Ignore;

@Ignore
public class DefaultRequestHandler implements RequestHandler {
	private final int status;

	public DefaultRequestHandler() {
		this(Response.Status.OK.getStatusCode());
	}

	public DefaultRequestHandler(int status) {
		this.status = status;
	}

	@Override
	public boolean matches(String path) {
		return true;
	}

	@Override
	public Response getResponse(Request request) {
		return Response.status(status).build();
	}
}
