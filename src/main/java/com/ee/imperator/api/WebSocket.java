package com.ee.imperator.api;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.websocket.Session;

import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

public class WebSocket extends ApiImplementation {
	private final Map<Game, Map<Session, Long>> sessions;

	WebSocket() {
		sessions = new WeakHashMap<>();
		Api.INSTANCE.addRequestListener(new WebSocketListener(this));
	}

	Map<Game, Map<Session, Long>> getSessions() {
		return sessions;
	}

	public String handle(Member member, JSONObject input) {
		try {
			JSONObject response = handleRequest(getVariables(input), member);
			return response == null ? null : response.toString();
		} catch(RequestException e) {
			return new JSONObject().put("type", e.getType()).put("mode", e.getMode()).put("error", e.getMessage(member.getLanguage())).toString();
		}
	}

	private Map<String, Object> getVariables(JSONObject input) {
		Map<String, Object> out = new HashMap<>();
		for(String key : input.keySet()) {
			out.put(key, input.get(key));
		}
		return out;
	}

	public void register(Session session, int gid, long time) {
		if(time == 0) {
			time = System.currentTimeMillis();
		}
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

	void update(Game game, Session session, long time) {
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
