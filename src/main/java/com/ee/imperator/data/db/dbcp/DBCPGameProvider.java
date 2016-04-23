package com.ee.imperator.data.db.dbcp;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.cache.CachedGameProvider;
import com.ee.imperator.data.db.SqlGameProvider;

public class DBCPGameProvider extends CachedGameProvider {
	public DBCPGameProvider() {
		super(new SqlGameProvider(createDataSource()));
	}

	private static DataSource createDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(getConfigOrCrash("driver"));
		dataSource.setUrl(getConfigOrCrash("url"));
		dataSource.setUsername(Imperator.getConfig().getString(DBCPGameProvider.class, "username"));
		dataSource.setPassword(Imperator.getConfig().getString(DBCPGameProvider.class, "password"));
		dataSource.setDefaultAutoCommit(false);
		dataSource.setRollbackOnReturn(true);
		return dataSource;
	}

	private static String getConfigOrCrash(String key) {
		String value = Imperator.getConfig().getString(DBCPGameProvider.class, key);
		if(value == null || value.isEmpty()) {
			throw new NullPointerException("Missing config value for " + key);
		}
		return value;
	}
}
