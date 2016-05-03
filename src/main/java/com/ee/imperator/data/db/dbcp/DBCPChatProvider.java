package com.ee.imperator.data.db.dbcp;

import com.ee.imperator.data.db.SqlChatProvider;

public class DBCPChatProvider extends SqlChatProvider {
	public DBCPChatProvider() {
		super(DBCPProvider.getDataSource());
	}
}
