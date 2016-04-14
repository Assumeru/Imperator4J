package com.ee.imperator.request.page;

import javax.ws.rs.core.Response;

import org.ee.web.request.page.Ignore;

@Ignore
public class Http404 extends ErrorPage {
	public Http404() {
		super(Response.Status.NOT_FOUND, "404 Not found", "The specified page could not be found.");
	}
}
