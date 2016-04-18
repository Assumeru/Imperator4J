package org.ee.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementBuilder implements Appendable, CharSequence {
	private final StringBuilder statement;

	public PreparedStatementBuilder() {
		statement = new StringBuilder();
	}

	public PreparedStatementBuilder(CharSequence sql) {
		statement = new StringBuilder(sql);
	}

	public PreparedStatement build(Connection conn) throws SQLException {
		return conn.prepareStatement(statement.toString());
	}

	public PreparedStatementBuilder appendParameters(int numberOfParameters) {
		if(numberOfParameters < 0) {
			throw new IllegalArgumentException("numberOfParameters < 0");
		} else if(numberOfParameters > 0) {
			statement.append("?");
			for(int i = 1; i < numberOfParameters; i++) {
				statement.append(", ?");
			}
		}
		return this;
	}

	@Override
	public PreparedStatementBuilder append(CharSequence csq) {
		statement.append(csq);
		return this;
	}

	@Override
	public PreparedStatementBuilder append(CharSequence csq, int start, int end) {
		statement.append(csq, start, end);
		return this;
	}

	@Override
	public PreparedStatementBuilder append(char c) {
		statement.append(c);
		return this;
	}

	@Override
	public int length() {
		return statement.length();
	}

	@Override
	public char charAt(int index) {
		return statement.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return statement.subSequence(start, end);
	}

	@Override
	public String toString() {
		return statement.toString();
	}
}
