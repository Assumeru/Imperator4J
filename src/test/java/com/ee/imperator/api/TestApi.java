package com.ee.imperator.api;

import java.util.Map;

import org.ee.collection.MapBuilder;

import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

public class TestApi {
	public static final TestApi INSTANCE = new TestApi();

	public static class Context {
		private final Member member;
		private final Map<String, String> variables;

		public Context(Member member, Map<String, String> variables) {
			this.member = member;
			this.variables = variables;
		}

		public Map<String, String> getVariables() {
			return variables;
		}

		public Member getMember() {
			return member;
		}
	}

	public static class EndTurn extends Context {
		public EndTurn(Player player, Card card) {
			super(player.getMember(), new MapBuilder<String, String>()
					.put("mode", "game")
					.put("type", "end-turn")
					.put("gid", String.valueOf(player.getGame().getId()))
					.put("card", card == null ? "-1" : String.valueOf(card.ordinal())).build());
		}
	}

	public static class Fortify extends Context {
		public Fortify(Player player) {
			super(player.getMember(), new MapBuilder<String, String>()
					.put("mode", "game")
					.put("type", "fortify")
					.put("gid", String.valueOf(player.getGame().getId())).build());
		}
	}

	public static class Stack extends Context {
		public Stack(Player player, Territory territory, int units) {
			super(player.getMember(), new MapBuilder<String, String>()
					.put("mode", "game")
					.put("type", "place-units")
					.put("gid", String.valueOf(player.getGame().getId()))
					.put("territory", territory.getId())
					.put("units", String.valueOf(units)).build());
		}
	}

	public static class Attack extends Context {
		public Attack(Player player, Territory from, Territory to, int units, int move) {
			super(player.getMember(), new MapBuilder<String, String>()
					.put("mode", "game")
					.put("type", "attack")
					.put("gid", String.valueOf(player.getGame().getId()))
					.put("from", from.getId())
					.put("to", to.getId())
					.put("units", String.valueOf(units))
					.put("move", String.valueOf(move)).build());
		}
	}

	public static class StartMove extends Context {
		public StartMove(Player player) {
			super(player.getMember(), new MapBuilder<String, String>()
					.put("mode", "game")
					.put("type", "start-move")
					.put("gid", String.valueOf(player.getGame().getId())).build());
		}
	}

	public static class MoveUnits extends Context {
		public MoveUnits(Player player, Territory from, Territory to, int move) {
			super(player.getMember(), new MapBuilder<String, String>()
					.put("mode", "game")
					.put("type", "move")
					.put("gid", String.valueOf(player.getGame().getId()))
					.put("from", from.getId())
					.put("to", to.getId())
					.put("move", String.valueOf(move)).build());
		}
	}

	public static class PlayCards extends Context {
		public PlayCards(Player player, int units) {
			super(player.getMember(), new MapBuilder<String, String>()
					.put("mode", "game")
					.put("type", "play-cards")
					.put("gid", String.valueOf(player.getGame().getId()))
					.put("units", String.valueOf(units)).build());
		}
	}

	private TestApi() {
	}

	public String handle(com.ee.imperator.api.TestApi.Context input) {
		try {
			return Api.handleRequest(input.getVariables(), input.getMember());
		} catch (RequestException e) {
			throw new RuntimeException(e);
		}
	}
}
