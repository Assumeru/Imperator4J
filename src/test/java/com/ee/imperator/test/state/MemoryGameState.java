package com.ee.imperator.test.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.ee.imperator.data.GameState;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

public class MemoryGameState implements GameState {
	private final java.util.Map<Integer, Game> games = new HashMap<>();
	private int id = 1;

	@Override
	public void close() throws IOException {
	}

	@Override
	public List<Game> getGames() {
		return new ArrayList<>(games.values());
	}

	@Override
	public List<Game> getGames(User user) {
		return Collections.emptyList();
	}

	@Override
	public Game getGame(int id) {
		return games.get(id);
	}

	@Override
	public Game createGame(Player owner, Map map, String name, String password) {
		Game game = new Game(id++, map.copy(), name, owner, password, System.currentTimeMillis());
		games.put(game.getId(), game);
		return game;
	}

	@Override
	public void deleteGame(Game game) {
		games.remove(game.getId());
	}

	@Override
	public List<LogEntry> getCombatLogs(Game game, long time) {
		return Collections.emptyList();
	}

	@Override
	public GameTransaction modify(Game game) throws TransactionException {
		return new MemoryGameTransaction(game);
	}

	@Override
	public Collection<Integer> deleteOldGames(long finishedTime, long time) {
		return Collections.emptySet();
	}
}
