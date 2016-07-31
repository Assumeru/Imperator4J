package com.ee.imperator.web.page;

import org.ee.web.Status;

import com.ee.imperator.web.Ignore;

@Ignore
public class Http500 extends ErrorPage {
	public Http500() {
		super(Status.INTERNAL_SERVER_ERROR, "500 Internal Server Error", "An error occurred while loading this page.");
	}
}
