package com.ee.imperator.data.transaction;

import com.ee.imperator.exception.TransactionException;

public interface Transaction extends AutoCloseable, Revertible {
	/**
	 * Commits all changes.
	 * 
	 * @throws TransactionException
	 */
	void commit() throws TransactionException;

	/**
	 * Releases the resources associated with this transaction without committing changes.
	 * Also closes all child transactions.
	 * 
	 * @throws TransactionException
	 */
	@Override
	void close() throws TransactionException;
}
