package org.ee.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder {
	private final StringBuilder query;
	private final List<Argument<?>> arguments;

	public UpdateBuilder(String table) {
		query = new StringBuilder("UPDATE ").append(table).append(" SET ");
		arguments = new ArrayList<>();
	}

	public <T> UpdateBuilder setColumn(String name, Column<T> setter, T value) {
		if(!arguments.isEmpty()) {
			query.append(", ");
		}
		query.append(name).append(" = ?");
		arguments.add(new Argument<>(setter, value));
		return this;
	}

	@Override
	public String toString() {
		return query.toString();
	}

	public PreparedStatement toStatement(Connection connection, String where) throws SQLException {
		query.append(" WHERE ").append(where);
		PreparedStatement statement = connection.prepareStatement(toString());
		for(int i = 0; i < arguments.size(); i++) {
			arguments.get(i).set(statement, i + 1);
		}
		return statement;
	}

	public int getArguments() {
		return arguments.size();
	}

	@FunctionalInterface
	public interface Column<T> {
		void set(PreparedStatement statement, int index, T value) throws SQLException;
	}

	private static class Argument<T> {
		private final Column<T> setter;
		private final T value;

		public Argument(Column<T> setter, T value) {
			this.setter = setter;
			this.value = value;
		}

		public void set(PreparedStatement statement, int index) throws SQLException {
			setter.set(statement, index, value);
		}
	}
}
