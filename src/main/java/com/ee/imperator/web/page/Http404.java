package com.ee.imperator.web.page;

import org.ee.web.Status;

import com.ee.imperator.web.Ignore;

@Ignore
public class Http404 extends ErrorPage {
	public Http404() {
		super(Status.NOT_FOUND, "404 Not found", "The specified page could not be found.");
	}
}
