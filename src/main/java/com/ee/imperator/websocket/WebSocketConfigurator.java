package com.ee.imperator.websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

import org.ee.web.request.Request;
import org.ee.web.request.servlet.ServletRequest;

import com.ee.imperator.ImperatorApplicationContext;

public class WebSocketConfigurator extends Configurator {
	private final ImperatorApplicationContext context;

	public WebSocketConfigurator(ImperatorApplicationContext context) {
		this.context = context;
	}

	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
		sec.getUserProperties().put(ImperatorApplicationContext.class.getName(), context);
		Request req = new ServletRequest(new WebSocketServletRequest(context.getContext(), request), null);
		sec.getUserProperties().put(Request.class.getName(), req);
	}
}
