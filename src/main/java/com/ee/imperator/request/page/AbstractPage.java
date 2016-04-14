package com.ee.imperator.request.page;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.ee.web.request.Request;
import org.ee.web.request.page.NavigationPage;
import org.ee.web.request.page.WebPage;

public abstract class AbstractPage implements WebPage {
	private final String path;
	private final int status;
	private final String template;
	private final String title;

	protected AbstractPage(String path, String template) {
		this(path, template, null);
	}

	protected AbstractPage(String path, String template, String title) {
		this(path, template, Response.Status.OK, title);
	}

	protected AbstractPage(String path, String template, Response.Status status, String title) {
		this(path, template, status.getStatusCode(), title);
	}

	protected AbstractPage(String path, String template, int status, String title) {
		this.path = path;
		this.status = status;
		this.template = template + "::fragment";
		this.title = title;
	}

	protected String getTitle() {
		return title;
	}

	protected int getStatus() {
		return status;
	}

	protected String getTemplate() {
		return template;
	}

	@Override
	public String getPath() {
		return path;
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

	protected String getBodyTemplate(PageContext context) {
		return template;
	}

	protected void setDefaultVariables(PageContext context) {
		final String template = getBodyTemplate(context);
		if(template != null) {
			context.setVariable(PageContext.VARIABLE_BODY, template);
		}
		context.setVariable(PageContext.VARIABLE_TITLE, title);
		context.setVariable(PageContext.VARIABLE_JAVASCRIPT_SETTINGS, getJavaScriptSettingsInternal());
		for(Variable<?> var : PageContext.DEFAULT_VARIABLES) {
			context.setVariable(var);
		}
	}

	private Map<String, Object> getJavaScriptSettingsInternal() {
		Map<String, Object> out = new HashMap<>();
		out.put("settings", getJavaScriptSettings());
		return out;
	}

	protected Map<String, Object> getJavaScriptSettings() {
		return Collections.emptyMap();
	}

	@Override
	public Response getResponse(Request request) {
		PageContext context = (PageContext) request.getContext();
		setDefaultVariables(context);
		setVariables(context);
		final ByteArrayOutputStream response = context.processPage();
		return Response.status(status).entity(new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				response.writeTo(output);
				output.flush();
			}
		}).build();
	}

	protected abstract void setVariables(PageContext context);

	@Override
	public boolean matches(String path) {
		return path.startsWith(getPath());
	}
}
