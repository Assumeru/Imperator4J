package com.ee.imperator.data.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.ee.collection.ChangeCollection;

import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.Game.State;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public abstract class AbstractGameTransaction<E> implements GameTransaction {
	protected final Game game;
	protected final List<E> children;
	private final Map<String, TerritoryTransaction> territoryCache;
	private final Map<Integer, PlayerTransaction> playerCache;
	private final List<LogEntry> logEntries;
	private final ChangeCollection<Attack> attacks;
	private final ChangeCollection<Player> players;
	private int units;
	private State state;
	private boolean conquered;
	private Player currentTurn;
	private long time;
	private boolean deleteLogEntries;
	private boolean deleteTerritories;

	public AbstractGameTransaction(Game game) {
		this.game = game;
		children = new ArrayList<>();
		territoryCache = new HashMap<>();
		playerCache = new HashMap<>();
		logEntries = new ArrayList<>();
		synchronized(game.getAttacks()) {
			attacks = new ChangeCollection<>(new HashSet<>(game.getAttacks()));
		}
		players = new ChangeCollection<>(new ArrayList<>(game.getPlayers()));
		setValues();
	}

	private void setValues() {
		units = game.getUnits();
		state = game.getState();
		conquered = game.hasConquered();
		currentTurn = game.getCurrentPlayer();
		time = game.getTime();
	}

	@Override
	public void revert() throws TransactionException {
		setValues();
		logEntries.clear();
		deleteLogEntries = false;
		deleteTerritories = false;
	}

	@Override
	public void addEntry(LogEntry entry) {
		logEntries.add(entry);
	}

	@Override
	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public void setCurrentTurn(Player currentTurn) {
		this.currentTurn = currentTurn;
	}

	@Override
	public void setConquered(boolean conquered) {
		this.conquered = conquered;
	}

	@Override
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public void setUnits(int units) {
		this.units = units;
	}

	@Override
	public void deleteLogEntries() {
		deleteLogEntries = true;
	}

	@Override
	public void deleteTerritories() {
		deleteTerritories = true;
	}

	@Override
	public void addPlayer(Player player) {
		players.add(player);
	}

	@Override
	public void removePlayer(Player player) {
		players.remove(player);
	}

	protected List<LogEntry> getLogEntries() {
		return logEntries;
	}

	protected int getUnits() {
		return units;
	}

	protected State getState() {
		return state;
	}

	protected boolean hasConquered() {
		return conquered;
	}

	protected Player getCurrentTurn() {
		return currentTurn;
	}

	protected long getTime() {
		return time;
	}

	protected boolean willDeleteLogEntries() {
		return deleteLogEntries;
	}

	protected boolean willDeleteTerritories() {
		return deleteTerritories;
	}

	@Override
	public Collection<Attack> getAttacks() {
		return attacks;
	}

	protected Collection<Attack> getAddedAttacks() {
		return attacks.getAdded();
	}

	protected Collection<Attack> getRemovedAttacks() {
		return attacks.getRemoved();
	}

	protected Collection<Player> getAddedPlayers() {
		return players.getAdded();
	}

	protected Collection<Player> getRemovedPlayers() {
		return players.getRemoved();
	}

	/**
	 * Default implementation writes to underlying game.
	 * @see GameTransaction#commit
	 */
	@Override
	public void commit() throws TransactionException {
		game.setConquered(conquered);
		game.setCurrentTurn(currentTurn);
		game.setState(state);
		game.setTime(time);
		game.setUnits(units);
		game.getAttacks().removeAll(attacks.getRemoved());
		game.getAttacks().addAll(attacks.getAdded());
	}

	/**
	 * No-op implementation.
	 * @see GameTransaction#close
	 */
	@Override
	public void close() throws TransactionException {
	}

	@SuppressWarnings("unchecked")
	@Override
	public TerritoryTransaction getTerritory(String id) throws NoSuchElementException, TransactionException {
		if(territoryCache.containsKey(id)) {
			return territoryCache.get(id);
		}
		Territory territory = game.getMap().getTerritories().get(id);
		if(territory == null) {
			throw new NoSuchElementException("No such territory " + id + " in " + game.getId());
		}
		TerritoryTransaction transaction = getTransaction(territory);
		territoryCache.put(id, transaction);
		children.add((E) transaction);
		return transaction;
	}

	protected abstract TerritoryTransaction getTransaction(Territory territory);

	@SuppressWarnings("unchecked")
	@Override
	public PlayerTransaction getPlayer(int id) throws NoSuchElementException, TransactionException {
		if(playerCache.containsKey(id)) {
			return playerCache.get(id);
		}
		Player player = game.getPlayerById(id);
		if(player == null) {
			throw new NoSuchElementException("No such player " + id + " in " + game.getId());
		}
		PlayerTransaction transaction = getTransaction(player);
		playerCache.put(id, transaction);
		children.add((E) transaction);
		return transaction;
	}

	protected abstract PlayerTransaction getTransaction(Player player);
}
