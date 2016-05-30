package com.ee.imperator.request.page;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.ee.collection.MapBuilder;
import org.ee.web.request.Request;
import org.ee.web.request.page.AbstractWebPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.api.WebSocket;
import com.ee.imperator.exception.PageException;
import com.ee.imperator.request.context.PageContext;
import com.ee.imperator.request.context.Variable;
import com.ee.imperator.websocket.WebSocketConfig;

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
		context.setVariable(PageContext.VARIABLE_JAVASCRIPT_SETTINGS, new MapBuilder<>().put("settings", PageContext.VARIABLE_JAVASCRIPT_SETTINGS.getDefaultValue(context)).build());
		for(Variable<?> var : PageContext.DEFAULT_VARIABLES) {
			context.setVariable(var);
		}
		String langJS = Imperator.getLanguageProvider().getJavascript();
		if(langJS != null) {
			PageContext.VARIABLE_JAVASCRIPT.add(context, langJS);
		}
	}

	protected abstract void setVariables(PageContext context);

	@Override
	protected final ByteArrayOutputStream getResponseOutput(Request request) {
		PageContext context = (PageContext) request.getContext();
		setDefaultVariables(context);
		setVariables(context);
		return context.processPage();
	}

	protected final void redirect(String path) {
		try {
			throw new WebApplicationException(Response.seeOther(new URI(path)).build());
		} catch (URISyntaxException e) {
			throw new PageException("Failed to redirect to " + path, e);
		}
	}

	static void addApiJavascript(PageContext context, int gid) {
		PageContext.VARIABLE_JAVASCRIPT.addAll(context, "store.js", "api.js", "dialog.js");
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "gid", gid);
		Map<String, String> api = new HashMap<>();
		api.put("longpollingURL", Imperator.getUrlBuilder().buildLink("/" + Ajax.PATH));
		if(Imperator.getConfig().getBoolean(WebSocket.class, "enabled", true)) {
			api.put("webSocketURL", Imperator.getUrlBuilder().buildLink(WebSocketConfig.PATH));
		}
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "API", api);
	}

	static void addChatJavascript(PageContext context, int gid, boolean canDelete) {
		addApiJavascript(context, gid);
		PageContext.VARIABLE_JAVASCRIPT.add(context, "chat.js");
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "chat", new MapBuilder<>().put("canDelete", canDelete).build());
	}
}
