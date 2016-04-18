package com.ee.imperator.data;

import java.util.List;

import com.ee.imperator.game.Game;

public interface GameProvider {
	List<Game> getGames();

	Game getGame(int id);
}
