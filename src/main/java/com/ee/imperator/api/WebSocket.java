package com.ee.imperator.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import javax.websocket.Session;

import org.ee.collection.MapBuilder;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

public class WebSocket {
	private static final Logger LOG = LogManager.createLogger();
	private final Map<Game, Map<Session, Long>> sessions;

	WebSocket() {
		sessions = new WeakHashMap<>();
	}

	public String handle(Member member, JSONObject input) {
		try {
			return Api.handleRequest(getVariables(input), member);
		} catch(RequestException e) {
			return new JSONObject().put("type", e.getType()).put("mode", e.getMode()).put("error", e.getMessage(member.getLanguage())).toString();
		}
	}

	public void trySendUpdates(JSONObject input) {
		try {
			sendUpdates(input);
		} catch(Exception e) {
			LOG.e("Error sending updates", e);
		}
	}

	private void sendUpdates(JSONObject input) {
		if(input.has("mode") && input.has("type") && input.has("gid")) {
			int gid = input.getInt("gid");
			if(shouldUpdate(input.getString("mode"), input.getString("type"))) {
				if(gid == 0) {
					sendChatUpdates();
				} else {
					sendGameUpdates(Imperator.getState().getGame(gid));
				}
			}
		}
	}

	private boolean shouldUpdate(String mode, String type) {
		if("chat".equals(mode) && "delete".equals(type)) {
			return false;
		} else if("game".equals(mode)) {
			return !"start-move".equals(type) && !"autoroll".equals(type);
		}
		return true;
	}

	private void sendGameUpdates(Game game) {
		Map<Session, Long> map = game == null ? null : sessions.get(game);
		if(map != null) {
			for(Entry<Session, Long> entry : map.entrySet()) {
				Member member = (Member) entry.getKey().getUserProperties().get(Member.class.getName());
				String type = (String) entry.getKey().getUserProperties().get(Game.State.class.getName());
				try {
					String response = sendGameUpdate(member, entry.getValue(), game, type);
					send(game, entry.getKey(), response);
				} catch(Exception e) {
					LOG.w("Failed to send update", e);
				}
			}
		}
	}

	private void sendChatUpdates() {
		Map<Session, Long> map = sessions.get(null);
		if(map != null) {
			for(Entry<Session, Long> entry : map.entrySet()) {
				Member member = (Member) entry.getKey().getUserProperties().get(Member.class.getName());
				try {
					String response = sendChatUpdate(member, entry.getValue());
					send(null, entry.getKey(), response);
				} catch(Exception e) {
					LOG.w("Failed to send update", e);
				}
			}
		}
	}

	private String sendGameUpdate(Member member, long time, Game game, String type) throws RequestException {
		return Api.handleRequest(new MapBuilder<String, String>()
				.put("mode", "update")
				.put("type", getType(game, type))
				.put("gid", String.valueOf(game.getId()))
				.put("time", String.valueOf(time))
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

	private String sendChatUpdate(Member member, long time) throws RequestException {
		return Api.handleRequest(new MapBuilder<String, String>()
				.put("mode", "update")
				.put("type", "chat")
				.put("time", String.valueOf(time))
				.build(), member);
	}

	private void send(Game game, Session session, String response) throws IOException {
		if(response != null) {
			session.getBasicRemote().sendText(response);
			long time = new JSONObject(response).getLong("update");
			update(game, session, time);
		}
	}

	private Map<String, String> getVariables(JSONObject input) {
		Map<String, String> out = new HashMap<>();
		for(String key : input.keySet()) {
			out.put(key, String.valueOf(input.get(key)));
		}
		return out;
	}

	public void register(Session session, int gid, long time) {
		Member member = (Member) session.getUserProperties().get(Member.class.getName());
		if(member != null && !member.isGuest()) {
			if(gid == 0) {
				update(null, session, time);
			} else {
				Game game = Imperator.getState().getGame(gid);
				if(game != null && game.getPlayers().contains(member)) {
					update(game, session, time);
				}
			}
		}
	}

	private synchronized Map<Session, Long> getGame(Game game) {
		Map<Session, Long> map = sessions.get(game);
		if(map == null) {
			map = new WeakHashMap<>();
			sessions.put(game, map);
		}
		return map;
	}

	private void update(Game game, Session session, long time) {
		Map<Session, Long> map = sessions.get(game);
		if(map == null) {
			map = getGame(game);
		}
		map.put(session, time);
	}

	public void deregister(Session session) {
		Integer gid = (Integer) session.getUserProperties().get(Game.class.getName());
		if(gid != null) {
			if(gid == 0) {
				remove(null, session);
			} else {
				Game game = Imperator.getState().getGame(gid);
				if(game != null) {
					remove(game, session);
				}
			}
		}
	}

	private void remove(Game game, Session session) {
		Map<Session, Long> map = sessions.get(game);
		if(map != null) {
			map.remove(session);
		}
	}
}
