package com.ee.imperator.template.thymeleaf;

import java.util.Map;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.url.UrlBuilder;

public class ThymeleafLinkBuilder extends StandardLinkBuilder {
	private final UrlBuilder urlBuilder;

	public ThymeleafLinkBuilder(ImperatorApplicationContext context) {
		urlBuilder = context.getUrlBuilder();
	}

	@Override
	protected String computeContextPath(IExpressionContext context, String base, Map<String, Object> parameters) {
		return urlBuilder.buildLink("");
	}
}
