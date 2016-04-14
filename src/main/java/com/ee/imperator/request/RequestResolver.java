package com.ee.imperator.request;

import org.ee.web.request.AbstractRequestResolver;
import org.ee.web.request.Request;
import org.ee.web.request.RequestHandler;
import org.ee.web.request.page.WebPage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.ee.imperator.request.page.Http404;
import com.ee.imperator.request.page.Http500;
import com.ee.imperator.request.page.PageContext;
import com.ee.imperator.request.thymeleaf.ThymeleafContext;
import com.ee.imperator.user.Member;

public class RequestResolver extends AbstractRequestResolver {
	private static final WebPage DEFAULT_PAGE = new Http404();
	private static final WebPage ERROR_PAGE = new Http500();
	private static TemplateEngine engine;

	@Override
	protected PageContext createContext(Request request) {
		return new ThymeleafContext(getTemplateEngine(), new WebContext(getRequest(), getResponse(), getServletContext()), new Member(), getNavigation(), request.getPath());
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
	protected RequestHandler getErrorHandler() {
		return ERROR_PAGE;
	}

	@Override
	protected RequestHandler getDefaultHandler() {
		return DEFAULT_PAGE;
	}

	@Override
	protected String[] getPackages() {
		return new String[] { getClass().getPackage().getName() };
	}
}
