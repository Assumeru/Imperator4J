package com.ee.imperator.data.db.dbcp;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.ConfigurationException;

public class DBCPProvider {
	private static volatile DataSource dataSource;

	private DBCPProvider() {}

	public static DataSource getDataSource() {
		if(dataSource == null) {
			createDataSource();
		}
		return dataSource;
	}

	private static synchronized void createDataSource() {
		if(DBCPProvider.dataSource == null) {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(getConfigOrCrash("driver"));
			dataSource.setUrl(getConfigOrCrash("url"));
			dataSource.setUsername(getConfigOrCrash("username"));
			dataSource.setPassword(Imperator.getConfig().getString(DBCPProvider.class, "password"));
			dataSource.setDefaultAutoCommit(false);
			dataSource.setRollbackOnReturn(true);
			DBCPProvider.dataSource = dataSource;
		}
	}

	private static String getConfigOrCrash(String key) {
		String value = Imperator.getConfig().getString(DBCPProvider.class, key);
		if(value == null || value.isEmpty()) {
			throw new ConfigurationException("Missing config value for " + key);
		}
		return value;
	}
}
