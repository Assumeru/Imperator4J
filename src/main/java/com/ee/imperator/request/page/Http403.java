package com.ee.imperator.request.page;

import javax.ws.rs.core.Response;

import org.ee.web.request.page.Ignore;

@Ignore
public class Http403 extends ErrorPage {
	public Http403() {
		super(Response.Status.FORBIDDEN, "403 Forbidden", "You are not allowed to view this page.");
	}
}
