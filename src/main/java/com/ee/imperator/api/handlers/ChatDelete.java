package com.ee.imperator.api.handlers;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Endpoint(mode = Mode.CHAT, type = "delete")
public class ChatDelete {
	private final ImperatorApplicationContext context;

	public ChatDelete(ImperatorApplicationContext context) {
		this.context = context;
	}

	public void handle(Member member, @Param("gid") int gid, @Param("time") long time) throws RequestException, TransactionException {
		Game game = gid == 0 ? null : context.getState().getGame(gid);
		if(!member.canDeleteMessages() && (game == null || !game.getOwner().equals(member))) {
			throw new InvalidRequestException("Cannot delete from chat", Mode.CHAT, "delete");
		}
		context.getState().deleteMessage(gid, time);
	}
}
