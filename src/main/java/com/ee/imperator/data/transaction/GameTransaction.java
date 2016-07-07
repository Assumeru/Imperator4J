package com.ee.imperator.data.transaction;

import java.util.Collection;
import java.util.NoSuchElementException;

import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Game.State;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public interface GameTransaction extends Transaction {
	void setTime(long time);

	void setCurrentTurn(Player currentTurn);

	void setConquered(boolean conquered);

	void setState(State state);

	void setUnits(int units);

	/**
	 * Creates a child transaction for one of this game's territories.
	 * 
	 * @param territory The territory's id
	 * @return A transaction for the given territory
	 * @throws NoSuchElementException If the territory does not exist
	 * @throws TransactionException If no transaction could be created
	 */
	TerritoryTransaction getTerritory(String territory) throws NoSuchElementException, TransactionException;

	default TerritoryTransaction getTerritory(Territory territory) throws NoSuchElementException, TransactionException {
		return getTerritory(territory.getId());
	}

	PlayerTransaction getPlayer(int player) throws NoSuchElementException, TransactionException;

	default PlayerTransaction getPlayer(Player player) throws NoSuchElementException, TransactionException {
		return getPlayer(player.getId());
	}

	void addEntry(LogEntry entry);

	void deleteTerritories();

	void deleteLogEntries();

	Collection<Attack> getAttacks();

	void addPlayer(Player player);

	void removePlayer(Player player);
}
