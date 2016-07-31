package com.ee.imperator.api;

import org.ee.collection.MapBuilder;

import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Player;

public class InternalApi extends ApiImplementation {
	InternalApi(Api api) {
		super(api);
	}

	public void joinGame(Game game, Player player) throws RequestException {
		handleRequest(new MapBuilder<String, Object>()
				.put("mode", "game")
				.put("type", "join")
				.put("gid", game.getId())
				.put("game", game)
				.put("player", player)
				.build(), player.getMember());
	}

	public void startGame(Game game) throws RequestException {
		handleRequest(new MapBuilder<String, Object>()
				.put("mode", "game")
				.put("type", "start")
				.put("gid", game.getId())
				.put("game", game)
				.build(), game.getOwner().getMember());
	}

	public void leaveGame(Game game, Player player) throws RequestException {
		handleRequest(new MapBuilder<String, Object>()
				.put("mode", "game")
				.put("type", "leave")
				.put("gid", game.getId())
				.put("game", game)
				.build(), player.getMember());
	}
}
