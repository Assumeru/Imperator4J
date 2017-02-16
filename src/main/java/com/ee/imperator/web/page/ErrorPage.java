package com.ee.imperator.web.page;

import org.ee.web.Status;

import com.ee.imperator.web.ImperatorRequestHandler;
import com.ee.imperator.web.context.PageContext;

public abstract class ErrorPage extends ImperatorPage {
	private final String description;

	protected ErrorPage(ImperatorRequestHandler handler, Status status, String title, String description) {
		super(handler, null, "errorpage", status, title);
		this.description = description;
	}

	@Override
	protected void setVariables(PageContext context) {
		context.setVariable("description", description);
	}
}
