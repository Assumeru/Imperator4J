package com.ee.imperator.template.thymeleaf;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ee.config.Config;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.template.Template;
import com.ee.imperator.template.TemplateProvider;

public class ThymeleafTemplateProvider implements TemplateProvider {
	private final TemplateEngine engine;

	public ThymeleafTemplateProvider(ImperatorApplicationContext context) {
		Config config = context.getConfig();
		engine = new TemplateEngine();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context.getContext());
		templateResolver.setTemplateMode(config.getString(ThymeleafTemplateProvider.class, "mode", TemplateMode.HTML.name()));
		templateResolver.setPrefix(config.getString(ThymeleafTemplateProvider.class, "path", "/WEB-INF/templates/"));
		templateResolver.setSuffix(config.getString(ThymeleafTemplateProvider.class, "suffix", ".html"));
		templateResolver.setCacheTTLMs(config.getLong(ThymeleafTemplateProvider.class, "cacheTTL", 3600000L));
		engine.setTemplateResolver(templateResolver);
		//TODO engine.setLinkBuilder()
	}

	@Override
	public Template createTemplate(String template) {
		return new ThymeleafTemplate(template, engine, new Context());
	}

	@Override
	public Template createTemplate(String template, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
		return new ThymeleafTemplate(template, engine, new WebContext(request, response, servletContext));
	}
}
