package com.ee.imperator.test.state;

import com.ee.imperator.data.transaction.AbstractTerritoryTransaction;
import com.ee.imperator.map.Territory;

public class MemoryTerritoryTransaction extends AbstractTerritoryTransaction implements MemoryChildTransaction {
	public MemoryTerritoryTransaction(Territory territory) {
		super(territory);
	}

	@Override
	public void apply() {
		super.apply();
	}
}
