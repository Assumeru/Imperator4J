package com.ee.imperator.request.page;

import javax.ws.rs.core.Response;

import org.ee.web.request.page.Ignore;

@Ignore
public class Http500 extends ErrorPage {
	public Http500() {
		super(Response.Status.INTERNAL_SERVER_ERROR, "500 Internal Server Error", "An error occurred while loading this page.");
	}
}
