package com.ee.imperator.web.page;

import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.web.ImperatorRequestHandler;
import com.ee.imperator.web.WebPage;

public class Ajax implements WebPage {
	private ImperatorApplicationContext context;

	@Override
	public Response handle(Request request) {
		return context.getApi().getLongPolling().handle(request);
	}

	@Override
	public boolean matches(Request request) {
		return request.getMethod() == Request.Method.POST && "ajax".equals(request.getPath()) || "ajax/".equals(request.getPath());
	}

	@Override
	public void setRequestHandler(ImperatorRequestHandler handler) {
		context = handler.getContext();
	}

	@Override
	public String getName() {
		return null;
	}
}
