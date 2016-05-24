package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Map;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

public interface GameState extends Closeable {
	List<Game> getGames();

	List<Game> getGames(User user);

	Game getGame(int id);

	Game createGame(Player owner, Map map, String name, String password);

	boolean addPlayerToGame(Player player, Game game);

	boolean removePlayerFromGame(Player player, Game game);

	boolean deleteGame(Game game);

	void startGame(Game game);

	List<LogEntry> getCombatLogs(Game game, long time);

	void setAutoRoll(Player player, boolean autoroll);

	boolean addCards(Player player, Card card, int amount);

	void startTurn(Player player);

	void updateUnitsAndState(Game game, Game.State state, int units);

	void placeUnits(Game game, Territory territory, int units);

	void forfeit(Player player);

	void saveAttack(Game game, Attack attack);

	void attack(Game game, Attack attack);

	void setState(Player player, Player.State state);

	void saveMissions(Game game);

	void setState(Game game, Game.State state);

	void moveUnits(Game game, Territory from, Territory to, int move);

	void playCards(Player player, int units);

	boolean victory(Player player);

	void deleteAttack(Attack attack);
}
