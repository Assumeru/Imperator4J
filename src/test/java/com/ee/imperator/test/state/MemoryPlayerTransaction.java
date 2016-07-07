package com.ee.imperator.test.state;

import com.ee.imperator.data.transaction.AbstractPlayerTransaction;
import com.ee.imperator.user.Player;

public class MemoryPlayerTransaction extends AbstractPlayerTransaction implements MemoryChildTransaction {
	public MemoryPlayerTransaction(Player player) {
		super(player);
	}

	@Override
	public void apply() {
		super.apply();
	}
}
