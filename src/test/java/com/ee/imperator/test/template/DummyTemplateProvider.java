package com.ee.imperator.test.template;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ee.imperator.template.Template;
import com.ee.imperator.template.TemplateProvider;

public class DummyTemplateProvider implements TemplateProvider {
	@Override
	public Template createTemplate(String template) {
		return null;
	}

	@Override
	public Template createTemplate(String template, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
		return null;
	}
}
