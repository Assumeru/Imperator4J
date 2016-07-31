package org.ee.web.request.filter;

import org.ee.web.request.Request;
import org.ee.web.request.RequestHandler;

public interface RequestFilter extends RequestHandler {
	boolean matches(Request request);
}
