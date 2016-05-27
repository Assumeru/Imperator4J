package com.ee.imperator.websocket;

import java.io.IOException;

import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.request.Request;
import org.json.JSONException;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.api.Api;
import com.ee.imperator.user.Member;

public class WebSocket {
	private static final Logger LOG = LogManager.createLogger();

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		Request request = (Request) session.getUserProperties().get(Request.class.getName());
		if(request != null) {
			Member member = Imperator.getState().getMember(request);
			if(!member.isGuest()) {
				session.getUserProperties().put(Member.class.getName(), member);
				return;
			}
		}
		try {
			session.close();
		} catch(IOException e) {
			LOG.e("Failed to close socket", e);
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			JSONObject variables = new JSONObject(message);
			String response = Api.WEB_SOCKET.handle((Member) session.getUserProperties().get(Member.class.getName()), variables);
			if(response != null) {
				session.getBasicRemote().sendText(response);
			}
		} catch(JSONException e) {
			LOG.w("Invalid message", e);
		} catch(IOException e) {
			LOG.i("Failed to send message", e);
		} catch(Exception e) {
			LOG.e("Unexpected error", e);
		}
	}
}
