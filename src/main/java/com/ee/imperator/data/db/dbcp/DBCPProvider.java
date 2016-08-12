package com.ee.imperator.data.db.dbcp;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.ee.imperator.ImperatorApplicationContext;

public class DBCPProvider {
	private static volatile DataSource dataSource;

	private DBCPProvider() {}

	public static DataSource getDataSource(ImperatorApplicationContext context) {
		if(dataSource == null) {
			createDataSource(context);
		}
		return dataSource;
	}

	private static synchronized void createDataSource(ImperatorApplicationContext context) {
		if(DBCPProvider.dataSource == null) {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(context.getStringSetting(DBCPProvider.class, "driver"));
			dataSource.setUrl(context.getStringSetting(DBCPProvider.class, "url"));
			dataSource.setUsername(context.getStringSetting(DBCPProvider.class, "username"));
			dataSource.setPassword(context.getConfig().getString(DBCPProvider.class, "password"));
			dataSource.setDefaultAutoCommit(false);
			dataSource.setRollbackOnReturn(true);
			DBCPProvider.dataSource = dataSource;
		}
	}
}
