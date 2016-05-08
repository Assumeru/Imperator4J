package com.ee.imperator.api.handlers;

import com.ee.imperator.Imperator;
import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Request(mode = "chat", type = "add")
public class ChatAdd {
	public void handle(Member member, @Param("gid") int gid, @Param("message") String message) throws RequestException {
		if(!ChatUpdate.canUseChat(member, gid)) {
			throw new InvalidRequestException(member.getId() + " cannot use chat " + gid, "update", "chat");
		}
		Game game = gid != 0 ? Imperator.getData().getGame(gid) : null;
		ChatMessage msg = new ChatMessage(game, member, System.currentTimeMillis(), message);
		if(!Imperator.getData().addMessage(msg)) {
			throw new RequestException("Failed to save message", "chat", "add");
		}
	}
}
