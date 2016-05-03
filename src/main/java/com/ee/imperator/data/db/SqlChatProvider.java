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
			PreparedStatement statement = conn.prepareStatement("SELECT `gid`, `uid`, `time`, `message` FROM `chat` WHERE `time` > ?");
			statement.setLong(1, time);
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				int gid = result.getInt(1);
				Game game = gid == 0 ? null : Imperator.getData().getGame(gid);
				int uid = result.getInt(2);
				User user = null;
				if(game != null) {
					user = game.getPlayerById(uid);
				}
				if(user == null) {
					user = Imperator.getData().getMember(uid);
				}
				messages.add(new ChatMessage(game, user, result.getLong(3), result.getString(4)));
			}
		} catch(SQLException e) {
			LOG.e("Failed to get chat messages", e);
		}
		return messages;
	}
}
