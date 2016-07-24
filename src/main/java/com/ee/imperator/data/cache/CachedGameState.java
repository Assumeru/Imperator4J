package com.ee.imperator.data.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ee.cache.SoftReferenceCache;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.BatchGameState;
import com.ee.imperator.data.GameState;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

public class CachedGameState implements GameState {
	private final Map<Integer, Game> cache;
	private final GameState gameProvider;

	public CachedGameState(GameState gameProvider, long timeToKeep) {
		this.gameProvider = gameProvider;
		cache = new SoftReferenceCache<>(timeToKeep);
	}

	public CachedGameState(GameState gameProvider) {
		this(gameProvider, Imperator.getConfig().getLong(CachedGameState.class, "timeToKeep"));
	}

	private void cache(Game game) {
		if(game != null) {
			cache.put(game.getId(), game);
		}
	}

	@Override
	public List<Game> getGames() {
		if(gameProvider instanceof BatchGameState) {
			return loadGames((BatchGameState) gameProvider);
		}
		List<Game> games = gameProvider.getGames();
		for(Game game : games) {
			cache(game);
		}
		return games;
	}

	@Override
	public List<Game> getGames(User user) {
		if(gameProvider instanceof BatchGameState) {
			return loadGames((BatchGameState) gameProvider, user);
		}
		List<Game> games = gameProvider.getGames(user);
		for(Game game : games) {
			cache(game);
		}
		return games;
	}

	private List<Game> loadGames(BatchGameState gameProvider) {
		return loadGames(gameProvider, gameProvider.getGameIds());
	}

	private List<Game> loadGames(BatchGameState gameProvider, User user) {
		return loadGames(gameProvider, gameProvider.getGameIds(user));
	}

	private List<Game> loadGames(BatchGameState gameProvider, Collection<Integer> ids) {
		List<Game> games = new ArrayList<>(ids.size());
		Map<Integer, Integer> toLoad = new HashMap<>();
		for(Integer id : ids) {
			Game game = cache.get(id);
			if(game != null) {
				games.add(game);
			} else {
				toLoad.put(id, games.size());
				games.add(null);
			}
		}
		if(!toLoad.isEmpty()) {
			Collection<Game> loaded = gameProvider.getGames(toLoad.keySet());
			for(Game game : loaded) {
				games.set(toLoad.get(game.getId()), game);
				cache(game);
			}
			if(loaded.size() != toLoad.size()) {
				//Remove games that couldn't be loaded
				games.removeIf(game -> game == null);
			}
		}
		return games;
	}

	@Override
	public Game getGame(int id) {
		Game game = cache.get(id);
		if(game == null) {
			game = gameProvider.getGame(id);
			cache(game);
		}
		return game;
	}

	@Override
	public void close() throws IOException {
		gameProvider.close();
	}

	@Override
	public Game createGame(Player owner, com.ee.imperator.map.Map map, String name, String password) throws TransactionException {
		Game game = gameProvider.createGame(owner, map, name, password);
		cache(game);
		return game;
	}

	@Override
	public void deleteGame(Game game) throws TransactionException {
		gameProvider.deleteGame(game);
		cache.remove(game.getId());
	}

	@Override
	public List<LogEntry> getCombatLogs(Game game, long time) {
		return gameProvider.getCombatLogs(game, time);
	}

	@Override
	public GameTransaction modify(Game game) throws TransactionException {
		return gameProvider.modify(game);
	}

	@Override
	public Collection<Integer> deleteOldGames(long finishedTime, long time) {
		Collection<Integer> deleted = gameProvider.deleteOldGames(finishedTime, time);
		for(Integer id : deleted) {
			cache.remove(id);
		}
		return deleted;
	}
}
