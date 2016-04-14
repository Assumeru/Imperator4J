package org.ee.web.request.page;

import org.ee.web.request.RequestHandler;

public interface WebPage extends RequestHandler {
	String getPath();

	String getName();
}
