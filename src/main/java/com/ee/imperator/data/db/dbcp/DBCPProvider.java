package com.ee.imperator.data.db.dbcp;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.ee.imperator.Imperator;

public class DBCPProvider {
	private static DataSource dataSource;

	public static DataSource getDataSource() {
		if(dataSource == null) {
			createDataSource();
		}
		return dataSource;
	}

	private synchronized static void createDataSource() {
		if(DBCPProvider.dataSource == null) {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(getConfigOrCrash("driver"));
			dataSource.setUrl(getConfigOrCrash("url"));
			dataSource.setUsername(Imperator.getConfig().getString(DBCPProvider.class, "username"));
			dataSource.setPassword(Imperator.getConfig().getString(DBCPProvider.class, "password"));
			dataSource.setDefaultAutoCommit(false);
			dataSource.setRollbackOnReturn(true);
			DBCPProvider.dataSource = dataSource;
		}
	}

	private static String getConfigOrCrash(String key) {
		String value = Imperator.getConfig().getString(DBCPProvider.class, key);
		if(value == null || value.isEmpty()) {
			throw new NullPointerException("Missing config value for " + key);
		}
		return value;
	}
}
