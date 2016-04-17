package com.ee.imperator.cache;

import org.ee.cache.SoftReferenceCache;

import com.ee.imperator.game.Game;

public class GameCache {
	private SoftReferenceCache<Integer, Game> cache;

	public GameCache(long timeToKeep) {
		cache = new SoftReferenceCache<>(timeToKeep);
	}

	public Game getGame(int id) {
		Game game = cache.get(id);
		if(game != null) {
			return game;
		}
		//TODO
		return null;
	}

	public void clear() {
		cache.clear();
	}
}
