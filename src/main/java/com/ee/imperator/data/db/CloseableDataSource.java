package com.ee.imperator.data.db;

import java.io.Closeable;
import java.io.IOException;

import javax.sql.DataSource;

public class CloseableDataSource implements Closeable {
	protected final DataSource dataSource;

	public CloseableDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void close() throws IOException {
		if(dataSource instanceof Closeable) {
			((Closeable) dataSource).close();
		} else if(dataSource instanceof AutoCloseable) {
			try {
				((AutoCloseable) dataSource).close();
			} catch (Exception e) {
				throw new IOException("Failed to close data source", e);
			}
		}
	}
}
