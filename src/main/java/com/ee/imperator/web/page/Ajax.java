package com.ee.imperator.web.page;

import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.imperator.api.LongPolling;
import com.ee.imperator.web.ImperatorRequestHandler;
import com.ee.imperator.web.WebPage;

public class Ajax implements WebPage {
	private LongPolling api;

	@Override
	public Response handle(Request request) {
		return api.handle(request);
	}

	@Override
	public boolean matches(Request request) {
		return "ajax".equals(request.getPath()) || "ajax/".equals(request.getPath());
	}

	@Override
	public void setRequestHandler(ImperatorRequestHandler handler) {
		api = handler.getContext().getApi().getLongPolling();
	}

	@Override
	public String getName() {
		return null;
	}
}
