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
import org.ee.sql.CloseableDataSource;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.data.ChatState;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.User;

public class SqlChatState extends CloseableDataSource implements ChatState {
	private static final Logger LOG = LogManager.createLogger();
	private final ImperatorApplicationContext context;

	public SqlChatState(DataSource dataSource, ImperatorApplicationContext context) {
		super(dataSource);
		this.context = context;
	}

	@Override
	public List<ChatMessage> getChatMessages(int id, long time) {
		List<ChatMessage> messages = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `uid`, `time`, `message` FROM `chat` WHERE `time` > ? AND `gid` = ? ORDER BY `time` ASC");
			statement.setLong(1, time);
			statement.setInt(2, id);
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				Game game = id == 0 ? null : context.getState().getGame(id);
				int uid = result.getInt(1);
				User user = null;
				if(game != null) {
					user = game.getPlayerById(uid);
				}
				if(user == null) {
					user = context.getState().getMember(uid);
				}
				messages.add(new ChatMessage(game, user, result.getLong(2), result.getString(3)));
			}
		} catch(SQLException e) {
			LOG.e("Failed to get chat messages", e);
		}
		return messages;
	}

	@Override
	public boolean hasChatMessages(int id, long time) {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT 1 FROM `chat` WHERE `time` > ? AND `gid` = ?");
			statement.setLong(1, time);
			statement.setInt(2, id);
			return statement.executeQuery().next();
		} catch(SQLException e) {
			LOG.e("Failed to check chat messages", e);
		}
		return false;
	}

	@Override
	public void addMessage(ChatMessage message) throws TransactionException {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("INSERT INTO `chat` (`gid`, `message`, `time`, `uid`) VALUES(?, ?, ?, ?)");
			int gid = message.getGame() == null ? 0 : message.getGame().getId();
			statement.setInt(1, gid);
			statement.setString(2, message.getMessage());
			statement.setLong(3, message.getTime());
			statement.setInt(4, message.getUser().getId());
			statement.execute();
			conn.commit();
		} catch(SQLException e) {
			throw new TransactionException("Failed to add chat message", e);
		}
	}

	@Override
	public void deleteMessage(int gid, long time) throws TransactionException {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM `chat` WHERE `gid` = ? AND `time` = ?");
			statement.setInt(1, gid);
			statement.setLong(2, time);
			statement.execute();
			conn.commit();
		} catch(SQLException e) {
			throw new TransactionException("Failed to delete chat message", e);
		}
	}

	@Override
	public int deleteOldMessages(long time, int keep) {
		try(Connection conn = dataSource.getConnection()) {
			ResultSet result = conn.createStatement().executeQuery("SELECT COUNT(1) FROM `chat` WHERE `gid` = 0");
			if(result.next()) {
				int limit = Math.max(0, result.getInt(1) - keep);
				if(limit > 0) {
					PreparedStatement statement = conn.prepareStatement("DELETE FROM `chat` WHERE `time` < ? AND `gid` = 0 ORDER BY `time` ASC LIMIT ?");
					statement.setLong(1, time);
					statement.setInt(2, limit);
					statement.execute();
					conn.commit();
					return limit;
				}
			}
		} catch(SQLException e) {
			LOG.e(e);
		}
		return 0;
	}
}
