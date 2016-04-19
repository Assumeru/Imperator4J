package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import com.ee.imperator.game.Game;

public interface GameProvider extends Closeable {
	List<Game> getGames();

	Game getGame(int id);
}
