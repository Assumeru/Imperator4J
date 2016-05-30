package com.ee.imperator.api;

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
	private Map<Game, Map<Session, Long>> sessions;

	WebSocket() {
		sessions = new WeakHashMap<>();
	}

	public String handle(Member member, JSONObject input) {
		try {
			String response = Api.handleRequest(getVariables(input), member);
			try {
				sendUpdates(input);
			} catch(Exception e) {
				LOG.e("Error sending updates", e);
			}
			return response;
		} catch(RequestException e) {
			return new JSONObject().put("type", e.getType()).put("mode", e.getMode()).put("error", e.getMessage(member.getLanguage())).toString();
		}
	}

	private void sendUpdates(JSONObject input) {
		if(input.has("mode") && input.has("type") && input.has("gid")) {
			int gid = input.getInt("gid");
			String mode = input.getString("mode");
			String type = input.getString("type");
			if(("chat".equals(mode) && "delete".equals(type)) ||
					("game".equals(mode) && ("start-move".equals(type) || "autoroll".equals(type)))) {
				return;
			}
			if(gid == 0) {
				sendChatUpdates();
			} else {
				Game game = Imperator.getState().getGame(gid);
				if(game != null) {
					sendGameUpdates(game);
				}
			}
		}
	}

	private void sendGameUpdates(Game game) {
		Map<Session, Long> map = sessions.get(game);
		if(map != null) {
			for(Entry<Session, Long> entry : map.entrySet()) {
				Member member = (Member) entry.getKey().getUserProperties().get(Member.class.getName());
				String response = sendGameUpdate(member, entry.getValue(), game.getId());
				send(game, entry.getKey(), response);
			}
		}
	}

	private void sendChatUpdates() {
		Map<Session, Long> map = sessions.get(null);
		if(map != null) {
			for(Entry<Session, Long> entry : map.entrySet()) {
				Member member = (Member) entry.getKey().getUserProperties().get(Member.class.getName());
				String response = sendChatUpdate(member, entry.getValue());
				send(null, entry.getKey(), response);
			}
		}
	}

	private String sendGameUpdate(Member member, long time, int gid) {
		try {
			return Api.handleRequest(new MapBuilder<String, String>()
					.put("mode", "update")
					.put("type", "game")
					.put("gid", String.valueOf(gid))
					.put("time", String.valueOf(time))
					.build(), member);
		} catch(Exception e) {
			LOG.w("Failed to send update", e);
		}
		return null;
	}

	private String sendChatUpdate(Member member, long time) {
		try {
			return Api.handleRequest(new MapBuilder<String, String>()
					.put("mode", "update")
					.put("type", "chat")
					.put("time", String.valueOf(time))
					.build(), member);
		} catch(Exception e) {
			LOG.w("Failed to send update", e);
		}
		return null;
	}

	private void send(Game game, Session session, String response) {
		if(response != null) {
			try {
				session.getBasicRemote().sendText(response);
				long time = new JSONObject(response).getLong("update");
				update(game, session, time);
			} catch(Exception e) {
				LOG.w("Failed to send update", e);
			}
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
