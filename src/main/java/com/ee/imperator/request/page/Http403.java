package com.ee.imperator.request.page;

import org.ee.web.request.page.Ignore;

@Ignore
public class Http403 extends ErrorPage {
	public Http403() {
		super(403, "403 Forbidden", "You are not allowed to view this page.");
	}
}
