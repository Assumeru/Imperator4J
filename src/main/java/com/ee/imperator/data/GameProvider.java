package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import com.ee.imperator.game.Game;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;

public interface GameProvider extends Closeable {
	List<Game> getGames();

	Game getGame(int id);

	Game createGame(Player owner, Map map, String name, String password);

	boolean addPlayerToGame(Player player, Game game);

	boolean removePlayerFromGame(Player player, Game game);

	boolean deleteGame(Game game);
}
