package org.ee.web.request;

import org.ee.web.response.Response;

public interface RequestHandler {
	Response handle(Request request);
}
