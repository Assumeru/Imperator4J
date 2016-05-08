package com.ee.imperator.template;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TemplateProvider {
	Template createTemplate(String template);

	Template createTemplate(String template, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext);
}
