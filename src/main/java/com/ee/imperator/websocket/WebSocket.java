package com.ee.imperator.websocket;

import java.io.IOException;
import java.util.Objects;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.request.Request;
import org.json.JSONException;
import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Endpoint;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

public class WebSocket {
	private static final Logger LOG = LogManager.createLogger();

	@OnOpen
	public void onOpen(Session session) {
		Request request = getProperty(session, Request.class);
		if(request != null) {
			Member member = getContext(session).getState().getMember(request);
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

	@SuppressWarnings("unchecked")
	private <T> T getProperty(Session session, Class<T> type) {
		return (T) session.getUserProperties().get(type.getName());
	}

	private ImperatorApplicationContext getContext(Session session) {
		return Objects.requireNonNull(getProperty(session, ImperatorApplicationContext.class));
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			JSONObject variables = new JSONObject(message);
			Member member = Objects.requireNonNull((Member) session.getUserProperties().get(Member.class.getName()));
			if((variables.has("mode") && Endpoint.Mode.of(variables.get("mode")) != Endpoint.Mode.UPDATE) || !session.getUserProperties().containsKey(Game.class.getName())) {
				if(!session.getUserProperties().containsKey(Game.class.getName())) {
					register(session, variables);
				}
				Object type = variables.get("type");
				if("pregame".equals(type) || "game".equals(type)) {
					session.getUserProperties().put(Game.State.class.getName(), type);
				}
				String response = getContext(session).getApi().getWebSocket().handle(member, variables);
				if(response != null) {
					session.getBasicRemote().sendText(response);
				}
			}
		} catch(JSONException e) {
			LOG.w("Invalid message", e);
		} catch(IOException e) {
			LOG.i("Failed to send message", e);
		} catch(Exception e) {
			LOG.e("Unexpected error", e);
		}
	}

	private void register(Session session, JSONObject variables) {
		if(variables.has("gid") && variables.has("time")) {
			try {
				session.getUserProperties().put(Game.class.getName(), variables.getInt("gid"));
				getContext(session).getApi().getWebSocket().register(session, variables.getInt("gid"), variables.getLong("time"));
			} catch(Exception e) {
				LOG.w("Error registering", e);
			}
		}
	}

	@OnClose
	public void onClose(Session session) {
		getContext(session).getApi().getWebSocket().deregister(session);
	}

	@OnError
	public void onError(Throwable e) {
		LOG.d(e);
	}
}
