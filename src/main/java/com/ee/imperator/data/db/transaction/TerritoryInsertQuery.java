package com.ee.imperator.data.db.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;

public class TerritoryInsertQuery {
	private final Connection connection;
	private final Game game;
	private final List<SqlInsertTerritoryTransaction> territories;
	private boolean committed;

	public TerritoryInsertQuery(Connection connection, Game game) {
		this.connection = connection;
		this.game = game;
		territories = new ArrayList<>();
	}

	public void register(SqlInsertTerritoryTransaction transaction) {
		territories.add(transaction);
	}

	public boolean isCommitted() {
		return committed;
	}

	public void commit() throws TransactionException {
		committed = true;
		territories.removeIf(t -> !t.hasChanged());
		try {
			StringBuilder query = new StringBuilder("INSERT INTO `territories` (`gid`, `territory`, `uid`, `units`) VALUES");
			for(int i = 0; i < territories.size(); i++) {
				if(i > 0) {
					query.append(", ");
				}
				query.append("(?, ?, ?, ?)");
			}
			PreparedStatement statement = connection.prepareStatement(query.toString());
			int i = 1;
			for(SqlInsertTerritoryTransaction territory : territories) {
				statement.setInt(i++, game.getId());
				statement.setString(i++, territory.getId());
				statement.setInt(i++, territory.getOwner().getId());
				statement.setInt(i++, territory.getUnits());
			}
			statement.execute();
		} catch (SQLException e) {
			throw new TransactionException("Failed to insert territories for " + game.getId(), e);
		}
	}
}
