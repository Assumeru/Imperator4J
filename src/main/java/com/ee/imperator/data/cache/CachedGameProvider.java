package com.ee.imperator.data.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ee.cache.SoftReferenceCache;

import com.ee.imperator.data.BatchGameProvider;
import com.ee.imperator.data.GameProvider;
import com.ee.imperator.game.Game;

public class CachedGameProvider implements GameProvider {
	private final SoftReferenceCache<Integer, Game> cache;
	private final GameProvider gameProvider;

	public CachedGameProvider(GameProvider gameProvider, long timeToKeep) {
		this.gameProvider = gameProvider;
		cache = new SoftReferenceCache<>(timeToKeep);
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
}
