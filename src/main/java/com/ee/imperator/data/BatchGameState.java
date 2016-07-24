package com.ee.imperator.data;

import java.util.Collection;

import com.ee.imperator.game.Game;
import com.ee.imperator.user.User;

/**
 * This interface allows for more efficient {@link GameState} implementations.
 */
public interface BatchGameState extends GameState {
	/**
	 * @return All game ids.
	 */
	Collection<Integer> getGameIds();

	/**
	 * Fetches multiple games by id.
	 * 
	 * @param ids The ids of the games to fetch
	 * @return A collection of games
	 */
	Collection<Game> getGames(Collection<Integer> ids);

	/**
	 * Fetches game ids for a given user.
	 * 
	 * @param user The user to get games for
	 * @return A list of game ids
	 */
	Collection<Integer> getGameIds(User user);
}
