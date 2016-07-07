package com.ee.imperator.test.state;

import com.ee.imperator.data.transaction.AbstractGameTransaction;
import com.ee.imperator.data.transaction.PlayerTransaction;
import com.ee.imperator.data.transaction.TerritoryTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public class MemoryGameTransaction extends AbstractGameTransaction<MemoryChildTransaction> {
	public MemoryGameTransaction(Game game) {
		super(game);
	}

	@Override
	public void commit() throws TransactionException {
		super.commit();
		for(MemoryChildTransaction child : children) {
			child.apply();
		}
	}

	@Override
	protected TerritoryTransaction getTransaction(Territory territory) {
		return new MemoryTerritoryTransaction(territory);
	}

	@Override
	protected PlayerTransaction getTransaction(Player player) {
		return new MemoryPlayerTransaction(player);
	}
}
