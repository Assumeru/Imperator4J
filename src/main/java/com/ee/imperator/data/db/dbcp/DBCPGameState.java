package com.ee.imperator.data.db.dbcp;

import com.ee.imperator.data.cache.CachedGameState;
import com.ee.imperator.data.db.SqlGameState;

public class DBCPGameState extends CachedGameState {
	public DBCPGameState() {
		super(new SqlGameState(DBCPProvider.getDataSource()));
	}
}
