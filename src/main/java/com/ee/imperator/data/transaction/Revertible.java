package com.ee.imperator.data.transaction;

import com.ee.imperator.exception.TransactionException;

public interface Revertible {
	/**
	 * Reverts all uncommitted changes.
	 * 
	 * @throws TransactionException
	 */
	void revert() throws TransactionException;
}
