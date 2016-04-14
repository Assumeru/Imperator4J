package com.ee.imperator.request.page;

import org.ee.web.request.page.Ignore;

@Ignore
public class Http500 extends ErrorPage {
	public Http500() {
		super(500, "500 Internal Server Error", "An error occurred while loading this page.");
	}
}
