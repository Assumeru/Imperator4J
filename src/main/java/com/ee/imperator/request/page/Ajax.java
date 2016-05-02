package com.ee.imperator.request.page;

import javax.ws.rs.core.Response;

import org.ee.web.request.Request;
import org.ee.web.request.page.WebPage;

import com.ee.imperator.api.Api;
import com.ee.imperator.request.context.PageContext;

public class Ajax implements WebPage {
	public static final String PATH = "ajax";

	@Override
	public Response getResponse(Request request) {
		return Api.LONG_POLLING.handle((PageContext) request.getContext());
	}

	@Override
	public boolean matches(String path) {
		return path.equals(PATH) || path.equals(PATH + "/");
	}

	@Override
	public String getPath() {
		return PATH;
	}

	@Override
	public String getName() {
		return "";
	}
}
