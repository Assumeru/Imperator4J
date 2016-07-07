package com.ee.imperator.data.transaction.sql;

import com.ee.imperator.data.transaction.Revertible;
import com.ee.imperator.exception.TransactionException;

public interface SqlChildTransaction extends Revertible {
	void apply();

	void commit() throws TransactionException;
}
