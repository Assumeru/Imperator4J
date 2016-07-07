package com.ee.imperator.data.transaction.sql;

import com.ee.imperator.data.transaction.AbstractTerritoryTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public class SqlInsertTerritoryTransaction extends AbstractTerritoryTransaction implements SqlChildTransaction {
	private final TerritoryInsertQuery query;

	public SqlInsertTerritoryTransaction(Territory territory, TerritoryInsertQuery query) {
		super(territory);
		query.register(this);
		this.query = query;
	}

	@Override
	public void apply() {
		super.apply();
	}

	@Override
	public void commit() throws TransactionException {
		if(!query.isCommitted()) {
			query.commit();
		}
	}

	public String getId() {
		return territory.getId();
	}

	public Player getOwner() {
		return super.getOwner();
	}

	public int getUnits() {
		return super.getUnits();
	}

	public boolean hasChanged() {
		return territory.getOwner() != getOwner();
	}
}
