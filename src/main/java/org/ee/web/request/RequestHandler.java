package org.ee.web.request;

import javax.ws.rs.core.Response;

public interface RequestHandler {
	boolean matches(String path);

	Response getResponse(Request request);
}
