package com.ee.imperator.request;

import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.ee.collection.MapBuilder;
import org.ee.web.request.AbstractRequestResolver;
import org.ee.web.request.Request;
import org.ee.web.request.RequestHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.ee.imperator.Imperator;
import com.ee.imperator.request.page.Http404;
import com.ee.imperator.request.page.Http500;
import com.ee.imperator.request.thymeleaf.ThymeleafContext;

public class RequestResolver extends AbstractRequestResolver {
	private static final Map<Integer, RequestHandler> STATUS_PAGES = new MapBuilder<Integer, RequestHandler>()
			.put(Status.NOT_FOUND.getStatusCode(), new Http404())
			.put(Status.INTERNAL_SERVER_ERROR.getStatusCode(), new Http500())
			.build(true);
	private static TemplateEngine engine;

	@Override
	protected PageContext createContext(Request request) {
		return new ThymeleafContext(getTemplateEngine(), new WebContext(getRequest(), getResponse(), getServletContext()), Imperator.getMember(request), getNavigation(), request.getPath());
	}

	private TemplateEngine getTemplateEngine() {
		if(engine == null) {
			initTemplateEngine();
		}
		return engine;
	}

	private synchronized void initTemplateEngine() {
		if(engine == null) {
			engine = new TemplateEngine();
			ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
			templateResolver.setTemplateMode(TemplateMode.HTML);
			templateResolver.setPrefix("/WEB-INF/templates/");
			templateResolver.setSuffix(".html");
			templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
			engine.setTemplateResolver(templateResolver);
		}
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
