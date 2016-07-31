package com.ee.imperator.data.db.dbcp;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ee.config.Config;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.exception.ConfigurationException;

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
			dataSource.setDriverClassName(getConfigOrCrash(context.getConfig(), "driver"));
			dataSource.setUrl(getConfigOrCrash(context.getConfig(), "url"));
			dataSource.setUsername(getConfigOrCrash(context.getConfig(), "username"));
			dataSource.setPassword(context.getConfig().getString(DBCPProvider.class, "password"));
			dataSource.setDefaultAutoCommit(false);
			dataSource.setRollbackOnReturn(true);
			DBCPProvider.dataSource = dataSource;
		}
	}

	private static String getConfigOrCrash(Config config, String key) {
		String value = config.getString(DBCPProvider.class, key);
		if(value == null || value.isEmpty()) {
			throw new ConfigurationException("Missing config value for " + key);
		}
		return value;
	}
}
