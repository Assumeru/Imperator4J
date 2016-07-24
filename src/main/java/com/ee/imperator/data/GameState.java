package com.ee.imperator.data;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;

import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

/**
 * This interface provides a way to persist game objects.
 * <p>
 * The game id {@code 0} is reserved.
 */
public interface GameState extends Closeable {
	/**
	 * Allows modification of a game.
	 * 
	 * @param game The game to modify
	 * @return A transaction to modify a game with
	 * @throws TransactionException If no transaction can be created
	 */
	GameTransaction modify(Game game) throws TransactionException;

	/**
	 * @return A list of all games.
	 */
	List<Game> getGames();

	/**
	 * Gets a list of games a given user participates in.
	 * 
	 * @param user A user to get games for
	 * @return A list of games
	 */
	List<Game> getGames(User user);

	/**
	 * Fetches a game by id.
	 * 
	 * @param id The (non-zero) id
	 * @return The game or {@code null} if the game does not exist
	 */
	Game getGame(int id);

	/**
	 * Creates a new game.
	 * 
	 * @param owner The owner of the game
	 * @param map The map to play on
	 * @param name The name of the game
	 * @param password The game's hashed password or {@code null} if the game has no password
	 * @return The newly-made game
	 * @throws TransactionException If the game could not be created
	 */
	Game createGame(Player owner, Map map, String name, String password) throws TransactionException;

	/**
	 * Deletes a game.
	 * 
	 * @param game The game to delete
	 * @throws TransactionException If the game could not be deleted
	 */
	void deleteGame(Game game) throws TransactionException;

	/**
	 * Returns a list of log entries made after a given time.
	 * 
	 * @param game The game to get entries for
	 * @param time The time to get entries after
	 * @return A list of log entries
	 */
	List<LogEntry> getCombatLogs(Game game, long time);

	/**
	 * Deletes old games.
	 * 
	 * @param finishedTime The maximum age of finished games (exclusive)
	 * @param time The maximum age to delete (exclusive)
	 * @return The ids of the deletes games
	 */
	Collection<Integer> deleteOldGames(long finishedTime, long time);
}
