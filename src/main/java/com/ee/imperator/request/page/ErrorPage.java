package com.ee.imperator.request.page;

import javax.ws.rs.core.Response;

import com.ee.imperator.request.context.PageContext;

public abstract class ErrorPage extends ImperatorPage {
	private final String description;

	protected ErrorPage(Response.Status status, String title, String description) {
		super(null, "errorpage", status, title);
		this.description = description;
	}

	@Override
	protected void setVariables(PageContext context) {
		context.setVariable("description", description);
	}
}
