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
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.Game.State;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Territory;
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
	public Game createGame(Player owner, com.ee.imperator.map.Map map, String name, String password) {
		Game game = gameProvider.createGame(owner, map, name, password);
		cache(game);
		return game;
	}

	@Override
	public boolean addPlayerToGame(Player player, Game game) {
		return gameProvider.addPlayerToGame(player, game);
	}

	@Override
	public boolean removePlayerFromGame(Player player, Game game) {
		return gameProvider.removePlayerFromGame(player, game);
	}

	@Override
	public boolean deleteGame(Game game) {
		if(gameProvider.deleteGame(game)) {
			cache.remove(game.getId());
			return true;
		}
		return false;
	}

	@Override
	public void startGame(Game game) {
		gameProvider.startGame(game);
	}

	@Override
	public List<LogEntry> getCombatLogs(Game game, long time) {
		return gameProvider.getCombatLogs(game, time);
	}

	@Override
	public void setAutoRoll(Player player, boolean autoroll) {
		gameProvider.setAutoRoll(player, autoroll);
	}

	@Override
	public boolean addCards(Player player, Card card, int amount) {
		return gameProvider.addCards(player, card, amount);
	}

	@Override
	public void startTurn(Player player) {
		gameProvider.startTurn(player);
	}

	@Override
	public void updateUnitsAndState(Game game, State state, int units) {
		gameProvider.updateUnitsAndState(game, state, units);
	}

	@Override
	public void placeUnits(Game game, Territory territory, int units) {
		gameProvider.placeUnits(game, territory, units);
	}

	@Override
	public void forfeit(Player player) {
		gameProvider.forfeit(player);
	}

	@Override
	public void saveAttack(Game game, Attack attack) {
		gameProvider.saveAttack(game, attack);
	}

	@Override
	public void attack(Game game, Attack attack) {
		gameProvider.attack(game, attack);
	}

	@Override
	public void setState(Player player, Player.State state) {
		gameProvider.setState(player, state);
	}

	@Override
	public void saveMissions(Game game) {
		gameProvider.saveMissions(game);
	}

	@Override
	public void setState(Game game, State state) {
		gameProvider.setState(game, state);
	}

	@Override
	public void moveUnits(Game game, Territory from, Territory to, int move) {
		gameProvider.moveUnits(game, from, to, move);
	}

	@Override
	public void playCards(Player player, int units) {
		gameProvider.playCards(player, units);
	}

	@Override
	public boolean victory(Player player) {
		return gameProvider.victory(player);
	}

	@Override
	public void deleteAttack(Attack attack) {
		gameProvider.deleteAttack(attack);
	}
}
