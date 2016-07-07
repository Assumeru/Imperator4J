package com.ee.imperator.data.transaction;

import com.ee.imperator.user.Player;

public interface TerritoryTransaction extends Revertible {
	void setOwner(Player owner);

	void setUnits(int units);
}
