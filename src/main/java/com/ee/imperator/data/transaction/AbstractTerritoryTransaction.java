package com.ee.imperator.data.transaction;

import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public class AbstractTerritoryTransaction implements TerritoryTransaction {
	protected final Territory territory;
	private Player owner;
	private int units;

	public AbstractTerritoryTransaction(Territory territory) {
		this.territory = territory;
		setValues();
	}

	private void setValues() {
		owner = territory.getOwner();
		units = territory.getUnits();
	}

	@Override
	public void revert() throws TransactionException {
		setValues();
	}

	@Override
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	@Override
	public void setUnits(int units) {
		this.units = units;
	}

	protected Player getOwner() {
		return owner;
	}

	protected int getUnits() {
		return units;
	}

	/**
	 * Writes changes to underlying territory.
	 */
	protected void apply() {
		territory.setOwner(owner);
		territory.setUnits(units);
	}
}
