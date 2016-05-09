package com.ee.imperator.api.handlers;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Request(mode = "chat", type = "delete")
public class ChatDelete {
	public void handle(Member member, @Param("gid") int gid, @Param("time") long time) throws RequestException {
		Game game = gid == 0 ? null : Imperator.getData().getGame(gid);
		if(!member.canDeleteMessages() && (game == null || !game.getOwner().equals(member))) {
			throw new InvalidRequestException("Cannot delete from chat", "chat", "delete");
		}
		if(!Imperator.getData().deleteMessage(gid, time)) {
			throw new RequestException("Failed to delete message", "chat", "delete");
		}
	}
}
