package com.ee.imperator.template.thymeleaf;

import java.util.Map;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;

import com.ee.imperator.ImperatorApplicationContext;

public class ThymeleafLinkBuilder extends StandardLinkBuilder {
	private final String basepath;

	public ThymeleafLinkBuilder(ImperatorApplicationContext context) {
		basepath = context.getConfig().getString(getClass(), "basepath");
	}

	@Override
	protected String computeContextPath(IExpressionContext context, String base, Map<String, Object> parameters) {
		return basepath + super.computeContextPath(context, base, parameters);
	}
}
