package com.ee.imperator.data.transaction.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.ee.sql.UpdateBuilder;

import com.ee.imperator.data.transaction.AbstractTerritoryTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;

public class SqlTerritoryTransaction extends AbstractTerritoryTransaction implements SqlChildTransaction {
	private final Game game;
	private final Connection connection;

	public SqlTerritoryTransaction(Connection connection, Game game, Territory territory) {
		super(territory);
		this.game = game;
		this.connection = connection;
	}

	@Override
	public void apply() {
		super.apply();
	}

	@Override
	public void commit() throws TransactionException {
		if(territory.getOwner() != getOwner() || territory.getUnits() != getUnits()) {
			UpdateBuilder update = new UpdateBuilder("`territories`");
			if(territory.getOwner() != getOwner()) {
				update.setColumn("`uid`", PreparedStatement::setInt, getOwner().getId());
			}
			if(territory.getUnits() != getUnits()) {
				update.setColumn("`units`", PreparedStatement::setInt, getUnits());
			}
			try {
				PreparedStatement statement = update.toStatement(connection, "WHERE `territory` = ? AND `gid` = ?");
				statement.setString(update.getArguments() + 1, territory.getId());
				statement.setInt(update.getArguments() + 2, game.getId());
				statement.execute();
			} catch (SQLException e) {
				throw new TransactionException("Failed to update territory " + territory.getId() + " in " + game.getId(), e);
			}
		}
	}
}
