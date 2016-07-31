package org.ee.web.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ee.web.ApplicationContext;

public interface RequestContext extends ApplicationContext {
	HttpServletRequest getServletRequest();

	HttpServletResponse getServletResponse();
}
