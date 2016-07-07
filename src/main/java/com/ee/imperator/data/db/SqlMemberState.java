package com.ee.imperator.data.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	public List<Member> getMembers() {
		List<Member> members = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			ResultSet result = conn.createStatement().executeQuery("SELECT `uid`, `wins`, `losses`, `score` FROM `users` ORDER BY `score`");
			while(result.next()) {
				members.add(new Member(result.getInt(1), null, null, false, false, result.getInt(4), result.getInt(2), result.getInt(3)));
			}
		} catch (SQLException e) {
			LOG.e("Error getting members", e);
		}
		return members;
	}
}
