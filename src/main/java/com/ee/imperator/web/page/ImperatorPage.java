package com.ee.imperator.web.page;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.ee.collection.MapBuilder;
import org.ee.web.Status;
import org.ee.web.exception.WebException;
import org.ee.web.request.Request;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;

import com.ee.imperator.api.WebSocket;
import com.ee.imperator.web.ImperatorRequestHandler;
import com.ee.imperator.web.context.DefaultPageContext;
import com.ee.imperator.web.context.PageContext;
import com.ee.imperator.web.context.Variable;

public abstract class ImperatorPage extends AbstractWebPage {
	private final String template;
	private final String title;

	protected ImperatorPage(ImperatorRequestHandler handler, String path, String template) {
		this(handler, path, template, null);
	}

	protected ImperatorPage(ImperatorRequestHandler handler, String path, String template, String title) {
		this(handler, path, template, Status.OK, title);
	}

	protected ImperatorPage(ImperatorRequestHandler handler, String path, String template, Status status, String title) {
		super(handler, path, status);
		this.template = template + "::fragment";
		this.title = title;
	}

	@Override
	protected final ByteArrayOutputStream getResponseOutput(Request request) {
		PageContext context = new DefaultPageContext(handler.getContext(), handler.getNavigationPages(), request);
		setDefaultVariables(context);
		setVariables(context);
		return context.processPage();
	}

	private void setDefaultVariables(PageContext context) {
		if(template != null) {
			context.setVariable(PageContext.VARIABLE_BODY, template);
		}
		context.setVariable(PageContext.VARIABLE_TITLE, title);
		context.setVariable(PageContext.VARIABLE_JAVASCRIPT_SETTINGS, new MapBuilder<>().put("settings", PageContext.VARIABLE_JAVASCRIPT_SETTINGS.getDefaultValue(context)).build());
		for(Variable<?> var : PageContext.DEFAULT_VARIABLES) {
			context.setVariable(var);
		}
		context.getLanguageProvider().addToPage(context);
		context.getCsrfTokenBuilder().addToPage(context);
	}

	protected abstract void setVariables(PageContext context);

	protected final void redirect(String path) {
		Response response = new SimpleResponse(Status.SEE_OTHER);
		response.setHeader("Location", handler.getContext().getContextPath() + path);
		throw new WebException(response);
	}

	static void addApiJavascript(PageContext context, int gid) {
		PageContext.VARIABLE_JAVASCRIPT.addAll(context, "store.js", "api.js", "dialog.js");
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "gid", gid);
		Map<String, String> api = new HashMap<>();
		api.put("longpollingURL", context.getUrlBuilder().buildLink("/ajax"));
		if(context.getConfig().getBoolean(WebSocket.class, "enabled", true)) {
			api.put("webSocketURL", context.getUrlBuilder().buildLink(WebSocket.PATH));
		}
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "API", api);
	}

	static void addChatJavascript(PageContext context, int gid, boolean canDelete) {
		addApiJavascript(context, gid);
		PageContext.VARIABLE_JAVASCRIPT.add(context, "chat.js");
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "chat", new MapBuilder<>().put("canDelete", canDelete).build());
	}
}
