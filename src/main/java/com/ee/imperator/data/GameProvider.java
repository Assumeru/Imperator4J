package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Map;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public interface GameProvider extends Closeable {
	List<Game> getGames();

	Game getGame(int id);

	Game createGame(Player owner, Map map, String name, String password);

	boolean addPlayerToGame(Player player, Game game);

	boolean removePlayerFromGame(Player player, Game game);

	boolean deleteGame(Game game);

	void startGame(Game game);

	void updateGameTime(Game game);

	List<LogEntry> getCombatLogs(Game game, long time);

	void setAutoRoll(Player player, boolean autoroll);

	boolean addCards(Player player, Card card, int amount);

	void startTurn(Player player);

	void updateUnitsAndState(Game game, Game.State state, int units);

	void placeUnits(Game game, Territory territory, int units);
}
