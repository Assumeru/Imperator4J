package com.ee.imperator.api.handlers;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

@Endpoint(mode = Mode.GAME, type = "kick")
public class Kick {
	private final ImperatorApplicationContext context;

	public Kick(ImperatorApplicationContext context) {
		this.context = context;
	}

	public void handle(Member member, @Param("gid") int gid, @Param("uid") int uid) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", Mode.GAME, "kick");
		} else if(!game.getOwner().equals(member)) {
			throw new InvalidRequestException("Only the game owner can kick players.", Mode.GAME, "kick");
		} else if(member.getId() == uid) {
			throw new InvalidRequestException("You cannot kick yourself.", Mode.GAME, "kick");
		} else if(game.hasStarted() || game.hasEnded()) {
			throw new InvalidRequestException("Cannot kick after starting", Mode.GAME, "kick");
		}
		Player player = game.getPlayerById(uid);
		if(player != null) {
			game.removePlayer(context, player);
		}
	}
}
