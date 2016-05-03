package com.ee.imperator.data.db.dbcp;

import com.ee.imperator.data.cache.CachedGameProvider;
import com.ee.imperator.data.db.SqlGameProvider;

public class DBCPGameProvider extends CachedGameProvider {
	public DBCPGameProvider() {
		super(new SqlGameProvider(DBCPProvider.getDataSource()));
	}
}
