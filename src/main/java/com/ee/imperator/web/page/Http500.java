package com.ee.imperator.web.page;

import org.ee.web.Status;

import com.ee.imperator.web.Ignore;
import com.ee.imperator.web.ImperatorRequestHandler;

@Ignore
public class Http500 extends ErrorPage {
	public Http500(ImperatorRequestHandler handler) {
		super(handler, Status.INTERNAL_SERVER_ERROR, "500 Internal Server Error", "An error occurred while loading this page.");
	}
}
