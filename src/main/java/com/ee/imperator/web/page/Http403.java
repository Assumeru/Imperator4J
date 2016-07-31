package com.ee.imperator.web.page;

import org.ee.web.Status;

import com.ee.imperator.web.Ignore;

@Ignore
public class Http403 extends ErrorPage {
	public Http403() {
		super(Status.FORBIDDEN, "403 Forbidden", "You are not allowed to view this page.");
	}
}
