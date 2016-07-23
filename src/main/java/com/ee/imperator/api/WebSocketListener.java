package com.ee.imperator.api;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.Session;

import org.ee.collection.MapBuilder;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

class WebSocketListener implements RequestListener {
	private static final Logger LOG = LogManager.createLogger();
	private final WebSocket api;

	WebSocketListener(WebSocket api) {
		this.api = api;
	}

	@Override
	public void onRequest(Member member, Map<String, ?> input, JSONObject output) {
		try {
			sendUpdates(member, input, output);
		} catch(Exception e) {
			LOG.e("Error sending updates", e);
		}
	}

	private void sendUpdates(Member member, Map<String, ?> input, JSONObject output) {
		if(input.containsKey("mode") && input.containsKey("type") && input.containsKey("gid")) {
			int gid = getInt(input.get("gid"));
			if(shouldUpdate(input.get("mode"), input.get("type"))) {
				if(gid == 0) {
					sendChatUpdates(member, output);
				} else {
					sendGameUpdates(member, output, Imperator.getState().getGame(gid));
				}
			}
		}
	}

	private int getInt(Object object) {
		if(object instanceof Integer) {
			return (Integer) object;
		}
		return Integer.parseInt(object.toString());
	}

	private boolean shouldUpdate(Object mode, Object type) {
		if("chat".equals(mode)) {
			return "add".equals(type);
		} else if("game".equals(mode)) {
			return !"start-move".equals(type) && !"autoroll".equals(type);
		}
		return false;
	}

	private void sendGameUpdates(Member cause, JSONObject output, Game game) {
		Map<Session, Long> map = game == null ? null : api.getSessions().get(game);
		if(map != null) {
			for(Entry<Session, Long> entry : map.entrySet()) {
				Member member = (Member) entry.getKey().getUserProperties().get(Member.class.getName());
				String type = (String) entry.getKey().getUserProperties().get(Game.State.class.getName());
				try {
					JSONObject response = sendGameUpdate(member, entry.getValue(), game, type);
					if(output != null && cause.equals(member)) {
						merge(output, response, entry);
						if(output.has("card") && output.has("cards")) {
							output.remove("cards");
						}
					} else {
						send(game, entry.getKey(), response);
					}
				} catch(Exception e) {
					LOG.w("Failed to send update", e);
				}
			}
		}
	}

	private void sendChatUpdates(Member cause, JSONObject output) {
		Map<Session, Long> map = api.getSessions().get(null);
		if(map != null) {
			for(Entry<Session, Long> entry : map.entrySet()) {
				Member member = (Member) entry.getKey().getUserProperties().get(Member.class.getName());
				try {
					JSONObject response = sendChatUpdate(member, entry.getValue());
					if(output != null && cause.equals(member)) {
						merge(output, response, entry);
					} else {
						send(null, entry.getKey(), response);
					}
				} catch(Exception e) {
					LOG.w("Failed to send update", e);
				}
			}
		}
	}

	private void merge(JSONObject output, JSONObject response, Entry<Session, Long> entry) {
		Long time = entry.getValue();
		if(response != null) {
			for(String key : response.keySet()) {
				if("update".equals(key)) {
					time = Math.max(Math.max(time, output.getLong(key)), response.getLong(key));
					output.put(key, time);
				} else {
					output.put(key, response.get(key));
				}
			}
		}
		entry.setValue(time);
	}

	private JSONObject sendGameUpdate(Member member, long time, Game game, String type) throws RequestException {
		return api.handleRequest(new MapBuilder<String, Object>()
				.put("mode", "update")
				.put("type", getType(game, type))
				.put("gid", game.getId())
				.put("time", time)
				.build(), member);
	}

	private String getType(Game game, String previous) {
		if(previous == null) {
			if(game.hasStarted()) {
				return "game";
			}
			return "pregame";
		}
		return previous;
	}

	private JSONObject sendChatUpdate(Member member, long time) throws RequestException {
		return api.handleRequest(new MapBuilder<String, Object>()
				.put("mode", "update")
				.put("type", "chat")
				.put("time", time)
				.put("gid", 0)
				.build(), member);
	}

	private void send(Game game, Session session, JSONObject response) throws IOException {
		if(response != null) {
			session.getBasicRemote().sendText(response.toString());
			long time = response.getLong("update");
			api.update(game, session, time);
		}
	}
}