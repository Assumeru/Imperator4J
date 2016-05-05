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

import com.ee.imperator.Imperator;
import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.data.ChatProvider;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.User;

public class SqlChatProvider implements ChatProvider {
	private static final Logger LOG = LogManager.createLogger();
	private final DataSource dataSource;

	public SqlChatProvider(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<ChatMessage> getChatMessages(int id, long time) {
		List<ChatMessage> messages = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `uid`, `time`, `message` FROM `chat` WHERE `time` > ? AND id = ?");
			statement.setLong(1, time);
			statement.setInt(2, id);
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				Game game = id == 0 ? null : Imperator.getData().getGame(id);
				int uid = result.getInt(1);
				User user = null;
				if(game != null) {
					user = game.getPlayerById(uid);
				}
				if(user == null) {
					user = Imperator.getData().getMember(uid);
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
}
