package com.ee.imperator.api.handlers;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Request(mode = "chat", type = "add")
public class ChatAdd {
	private final ImperatorApplicationContext context;

	public ChatAdd(ImperatorApplicationContext context) {
		this.context = context;
	}

	public void handle(Member member, @Param("gid") int gid, @Param("message") String message) throws RequestException, TransactionException {
		if(member.isGuest() || !ChatUpdate.canUseChat(member, gid, context)) {
			throw new InvalidRequestException(member.getId() + " cannot use chat " + gid, "update", "chat");
		}
		Game game = gid != 0 ? context.getState().getGame(gid) : null;
		ChatMessage msg = new ChatMessage(game, member, System.currentTimeMillis(), message);
		context.getState().addMessage(msg);
	}
}
