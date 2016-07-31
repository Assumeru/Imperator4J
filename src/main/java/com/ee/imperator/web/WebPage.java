package com.ee.imperator.web;

import org.ee.web.request.filter.RequestFilter;

public interface WebPage extends RequestFilter {
	void setRequestHandler(ImperatorRequestHandler handler);

	String getName();
}
