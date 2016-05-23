package com.ee.imperator.data;

import java.util.Collection;

import com.ee.imperator.game.Game;
import com.ee.imperator.user.User;

public interface BatchGameState extends GameState {
	Collection<Integer> getGameIds();

	Collection<Game> getGames(Collection<Integer> ids);

	Collection<Integer> getGameIds(User user);
}
