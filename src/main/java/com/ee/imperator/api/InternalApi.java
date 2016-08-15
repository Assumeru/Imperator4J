package com.ee.imperator.api;

import org.ee.collection.MapBuilder;

import com.ee.imperator.api.handlers.Endpoint;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Player;

/**
 * Convenience class for internal API calls.
 * Can interact with optimised handlers.
 */
public class InternalApi extends ApiImplementation {
	protected InternalApi(Api api) {
		super(api);
	}

	/**
	 * Adds a player to a game.
	 * 
	 * @param game The game
	 * @param player The player
	 * @throws RequestException If the player was unable to join
	 */
	public void joinGame(Game game, Player player) throws RequestException {
		handleRequest(new MapBuilder<String, Object>()
				.put("mode", Endpoint.Mode.GAME)
				.put("type", "join")
				.put("gid", game.getId())
				.put("game", game)
				.put("player", player)
				.build(), player.getMember());
	}

	/**
	 * Starts a game.
	 * 
	 * @param game The game to start
	 * @throws RequestException If the game could not be started
	 */
	public void startGame(Game game) throws RequestException {
		handleRequest(new MapBuilder<String, Object>()
				.put("mode", Endpoint.Mode.GAME)
				.put("type", "start")
				.put("gid", game.getId())
				.put("game", game)
				.build(), game.getOwner().getMember());
	}

	/**
	 * Removes a player from a game.
	 * 
	 * @param game The game
	 * @param player The player
	 * @throws RequestException If the player was unable to leave
	 */
	public void leaveGame(Game game, Player player) throws RequestException {
		handleRequest(new MapBuilder<String, Object>()
				.put("mode", Endpoint.Mode.GAME)
				.put("type", "leave")
				.put("gid", game.getId())
				.put("game", game)
				.build(), player.getMember());
	}
}
