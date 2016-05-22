package com.ee.imperator.data.db.dbcp;

import com.ee.imperator.data.db.SqlChatState;

public class DBCPChatState extends SqlChatState {
	public DBCPChatState() {
		super(DBCPProvider.getDataSource());
	}
}
