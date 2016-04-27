package com.ee.imperator.data.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ee.cache.SoftReferenceCache;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.BatchGameProvider;
import com.ee.imperator.data.GameProvider;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Player;

public class CachedGameProvider implements GameProvider {
	private final SoftReferenceCache<Integer, Game> cache;
	private final GameProvider gameProvider;

	public CachedGameProvider(GameProvider gameProvider, long timeToKeep) {
		this.gameProvider = gameProvider;
		cache = new SoftReferenceCache<>(timeToKeep);
	}

	public CachedGameProvider(GameProvider gameProvider) {
		this(gameProvider, Imperator.getConfig().getLong(CachedGameProvider.class, "timeToKeep"));
	}

	private void cache(Game game) {
		cache.put(game.getId(), game);
	}

	@Override
	public List<Game> getGames() {
		if(gameProvider instanceof BatchGameProvider) {
			return loadGames((BatchGameProvider) gameProvider);
		}
		List<Game> games = gameProvider.getGames();
		for(Game game : games) {
			cache(game);
		}
		return games;
	}

	private List<Game> loadGames(BatchGameProvider gameProvider) {
		Collection<Integer> ids = gameProvider.getGameIds();
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
			for(Game game : gameProvider.getGames(toLoad.keySet())) {
				games.set(toLoad.get(game.getId()), game);
				cache(game);
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
	public Game createGame(Player owner, com.ee.imperator.map.Map map, String name, String password) {
		Game game = gameProvider.createGame(owner, map, name, password);
		cache(game);
		return game;
	}

	@Override
	public boolean addPlayerToGame(Player player, Game game) {
		if(gameProvider.addPlayerToGame(player, game)) {
			game.addPlayer(player);
			return true;
		}
		return false;
	}

	@Override
	public boolean removePlayerFromGame(Player player, Game game) {
		if(gameProvider.removePlayerFromGame(player, game)) {
			game.removePlayer(player);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteGame(Game game) {
		if(gameProvider.deleteGame(game)) {
			cache.remove(game.getId());
			return true;
		}
		return false;
	}
}
