package com.ee.imperator.request;

import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.ee.collection.MapBuilder;
import org.ee.web.request.AbstractRequestResolver;
import org.ee.web.request.Request;
import org.ee.web.request.RequestHandler;

import com.ee.imperator.Imperator;
import com.ee.imperator.request.context.DefaultPageContext;
import com.ee.imperator.request.context.PageContext;
import com.ee.imperator.request.page.Http403;
import com.ee.imperator.request.page.Http404;
import com.ee.imperator.request.page.Http500;

public class RequestResolver extends AbstractRequestResolver {
	private static final Map<Integer, RequestHandler> STATUS_PAGES = new MapBuilder<Integer, RequestHandler>()
			.put(Status.FORBIDDEN.getStatusCode(), new Http403())
			.put(Status.NOT_FOUND.getStatusCode(), new Http404())
			.put(Status.INTERNAL_SERVER_ERROR.getStatusCode(), new Http500())
			.build(true);

	@Override
	protected PageContext createContext(Request request) {
		return new DefaultPageContext(Imperator.getTemplateProvider().createTemplate("page", getRequest(), getResponse(), getServletContext()), Imperator.getData().getMember(request), getNavigation(), request);
	}

	@Override
	protected String[] getPackages() {
		return new String[] { getClass().getPackage().getName() };
	}

	@Override
	protected RequestHandler getStatusPage(final int status) {
		return STATUS_PAGES.get(status);
	}
}
