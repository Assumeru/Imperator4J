package com.ee.imperator.data.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.request.Request;

import com.ee.imperator.data.MemberState;
import com.ee.imperator.user.Member;

public class SqlMemberState extends CloseableDataSource implements MemberState {
	private static final Logger LOG = LogManager.createLogger();

	public SqlMemberState(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Member getMember(int id) {
		Member member = new Member(id);
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `wins`, `losses`, `score` FROM `users` WHERE `uid` = ?");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			if(result.next()) {
				member.setWins(result.getInt(1));
				member.setLosses(result.getInt(2));
				member.setScore(result.getInt(3));
			}
		} catch (SQLException e) {
			LOG.e("Error getting member", e);
		}
		return member;
	}

	@Override
	public Member getMember(Request request) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getId(Request request) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addWin(Member member, int points) {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement;
			if(memberExists(conn, member)) {
				statement = conn.prepareStatement("UPDATE `users` SET `score` = `score` + ?, `wins` = `wins` + 1 WHERE `uid` = ?");
			} else {
				statement = conn.prepareStatement("INSERT INTO `users` (`score`, `wins`) VALUES(?, 1) WHERE `uid` = ?");
			}
			statement.setInt(1, points);
			statement.setInt(2, member.getId());
			statement.execute();
			conn.commit();
			member.setWins(member.getWins() + 1);
			member.setScore(member.getScore() + points);
		} catch (SQLException e) {
			LOG.e("Error adding win", e);
		}
	}

	@Override
	public void addLoss(Member member) {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement;
			if(memberExists(conn, member)) {
				statement = conn.prepareStatement("UPDATE `users` SET `score` = `score` - 1, `losses` = `losses` + 1 WHERE `uid` = ?");
			} else {
				statement = conn.prepareStatement("INSERT INTO `users` (`score`, `losses`) VALUES(-1, 1) WHERE `uid` = ?");
			}
			statement.setInt(1, member.getId());
			statement.execute();
			conn.commit();
			member.setLosses(member.getLosses() + 1);
			member.setScore(member.getScore() - 1);
		} catch (SQLException e) {
			LOG.e("Error adding loss", e);
		}
	}

	private boolean memberExists(Connection conn, Member member) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("SELECT 1 FROM `users` WHERE `uid` = ?");
		statement.setInt(1, member.getId());
		return statement.executeQuery().next();
	}
}
