package com.ee.imperator.test;

import java.util.Map;

import org.ee.collection.MapBuilder;
import org.json.JSONObject;

import com.ee.imperator.api.Api;
import com.ee.imperator.api.InternalApi;
import com.ee.imperator.api.handlers.Endpoint;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

public class TestApi extends InternalApi {
	public static class Context {
		private final Member member;
		private final Map<String, ?> variables;

		public Context(Member member, Map<String, ?> variables) {
			this.member = member;
			this.variables = variables;
		}

		public Map<String, ?> getVariables() {
			return variables;
		}

		public Member getMember() {
			return member;
		}
	}

	public static class EndTurn extends Context {
		public EndTurn(Player player, Card card) {
			super(player.getMember(), new MapBuilder<String, Object>()
					.put("mode", Endpoint.Mode.GAME)
					.put("type", "end-turn")
					.put("gid", player.getGame().getId())
					.put("card", card == null ? -1 : card.ordinal()).build());
		}
	}

	public static class Fortify extends Context {
		public Fortify(Player player) {
			super(player.getMember(), new MapBuilder<String, Object>()
					.put("mode", Endpoint.Mode.GAME)
					.put("type", "fortify")
					.put("gid", player.getGame().getId()).build());
		}
	}

	public static class Stack extends Context {
		public Stack(Player player, Territory territory, int units) {
			super(player.getMember(), new MapBuilder<String, Object>()
					.put("mode", Endpoint.Mode.GAME)
					.put("type", "place-units")
					.put("gid", player.getGame().getId())
					.put("territory", territory.getId())
					.put("units", units).build());
		}
	}

	public static class Attack extends Context {
		public Attack(Player player, Territory from, Territory to, int units, int move) {
			super(player.getMember(), new MapBuilder<String, Object>()
					.put("mode", Endpoint.Mode.GAME)
					.put("type", "attack")
					.put("gid", player.getGame().getId())
					.put("from", from.getId())
					.put("to", to.getId())
					.put("units", units)
					.put("move", move).build());
		}
	}

	public static class StartMove extends Context {
		public StartMove(Player player) {
			super(player.getMember(), new MapBuilder<String, Object>()
					.put("mode", Endpoint.Mode.GAME)
					.put("type", "start-move")
					.put("gid", player.getGame().getId()).build());
		}
	}

	public static class MoveUnits extends Context {
		public MoveUnits(Player player, Territory from, Territory to, int move) {
			super(player.getMember(), new MapBuilder<String, Object>()
					.put("mode", Endpoint.Mode.GAME)
					.put("type", "move")
					.put("gid", player.getGame().getId())
					.put("from", from.getId())
					.put("to", to.getId())
					.put("move", move).build());
		}
	}

	public static class PlayCards extends Context {
		public PlayCards(Player player, int units) {
			super(player.getMember(), new MapBuilder<String, Object>()
					.put("mode", Endpoint.Mode.GAME)
					.put("type", "play-cards")
					.put("gid", player.getGame().getId())
					.put("units", units).build());
		}
	}

	public static class Forfeit extends Context {
		public Forfeit(Player player) {
			super(player.getMember(), new MapBuilder<String, Object>()
					.put("mode", Endpoint.Mode.GAME)
					.put("type", "forfeit")
					.put("gid", player.getGame().getId()).build());
		}
	}

	public TestApi(Api api) {
		super(api);
	}

	public String handle(com.ee.imperator.test.TestApi.Context input) {
		try {
			JSONObject response = handleRequest(input.getVariables(), input.getMember());
			return response == null ? null : response.toString();
		} catch (RequestException e) {
			throw new RuntimeException(e);
		}
	}
}
