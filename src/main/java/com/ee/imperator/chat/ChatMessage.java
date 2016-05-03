package com.ee.imperator.chat;

import com.ee.imperator.game.Game;
import com.ee.imperator.user.User;

public class ChatMessage {
	private final Game game;
	private final User user;
	private final long time;
	private final String message;

	public ChatMessage(Game game, User user, long time, String message) {
		this.game = game;
		this.user = user;
		this.time = time;
		this.message = message;
	}

	public Game getGame() {
		return game;
	}

	public User getUser() {
		return user;
	}

	public long getTime() {
		return time;
	}

	public String getMessage() {
		return message;
	}
}
