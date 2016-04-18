package com.ee.imperator.data;

import java.util.Collection;

import com.ee.imperator.game.Game;

public interface BatchGameProvider extends GameProvider {
	Collection<Integer> getGameIds();

	Collection<Game> getGames(Collection<Integer> ids);
}
