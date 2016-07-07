package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

public interface GameState extends Closeable {
	GameTransaction modify(Game game) throws TransactionException;

	List<Game> getGames();

	List<Game> getGames(User user);

	Game getGame(int id);

	Game createGame(Player owner, Map map, String name, String password);

	boolean deleteGame(Game game);

	List<LogEntry> getCombatLogs(Game game, long time);
}
