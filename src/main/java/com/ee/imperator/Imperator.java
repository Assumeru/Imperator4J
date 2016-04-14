package com.ee.imperator;

import org.ee.web.WebApplication;
import org.ee.web.request.AbstractRequestResolver;

import com.ee.imperator.request.RequestResolver;

public class Imperator extends WebApplication {
	@Override
	protected Class<? extends AbstractRequestResolver> getRequestResolver() {
		return RequestResolver.class;
	}
}
