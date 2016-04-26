package com.ee.imperator.request.page;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.ee.collection.MapBuilder;
import org.ee.web.request.Request;
import org.ee.web.request.page.AbstractWebPage;

import com.ee.imperator.request.PageContext;

public abstract class ImperatorPage extends AbstractWebPage {
	private final String template;
	private final String title;

	protected ImperatorPage(String path, String template) {
		this(path, template, null);
	}

	protected ImperatorPage(String path, String template, String title) {
		this(path, template, Response.Status.OK, title);
	}

	protected ImperatorPage(String path, String template, Response.Status status, String title) {
		this(path, template, status.getStatusCode(), title);
	}

	protected ImperatorPage(String path, String template, int status, String title) {
		super(path, status);
		this.template = template + "::fragment";
		this.title = title;
	}

	protected String getTitle() {
		return title;
	}

	protected String getTemplate() {
		return template;
	}

	private void setDefaultVariables(PageContext context) {
		if(template != null) {
			context.setVariable(PageContext.VARIABLE_BODY, template);
		}
		context.setVariable(PageContext.VARIABLE_TITLE, title);
		context.setVariable(PageContext.VARIABLE_JAVASCRIPT_SETTINGS, new MapBuilder<String, Object>().put("settings", getJavaScriptSettings()).build());
		for(Variable<?> var : PageContext.DEFAULT_VARIABLES) {
			context.setVariable(var);
		}
	}

	protected Map<String, Object> getJavaScriptSettings() {
		return Collections.emptyMap();
	}

	protected abstract void setVariables(PageContext context);

	@Override
	protected final ByteArrayOutputStream getResponseOutput(Request request) {
		PageContext context = (PageContext) request.getContext();
		setDefaultVariables(context);
		setVariables(context);
		return context.processPage();
	}
}
