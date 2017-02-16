package com.ee.imperator.web.page;

import java.io.ByteArrayOutputStream;

import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;

import com.ee.imperator.web.ImperatorRequestHandler;
import com.ee.imperator.web.NavigationPage;
import com.ee.imperator.web.WebPage;

public abstract class AbstractWebPage implements WebPage {
	private final String path;
	private final Status status;
	protected final ImperatorRequestHandler handler;

	public AbstractWebPage(ImperatorRequestHandler handler, String path, Status status) {
		this.handler = handler;
		this.path = path;
		this.status = status;
	}

	@Override
	public Response handle(Request request) {
		Response response = new SimpleResponse(status, getResponseOutput(request));
		response.setContentType("text/html");
		return response;
	}

	protected abstract ByteArrayOutputStream getResponseOutput(Request request);

	@Override
	public boolean matches(Request request) {
		return request.getPath().equals(this.path) || request.getPath().equals(this.path + "/");
	}

	public String getPath() {
		return path;
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public String getName() {
		NavigationPage page = getClass().getAnnotation(NavigationPage.class);
		String name = null;
		if(page != null) {
			name = page.name();
		}
		return name == null ? "" : name;
	}
}
