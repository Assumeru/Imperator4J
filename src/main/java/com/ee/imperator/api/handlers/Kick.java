package com.ee.imperator.api.handlers;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

@Request(mode = "game", type = "kick")
public class Kick {
	public void handle(Member member, @Param("gid") int gid, @Param("uid") int uid) throws RequestException {
		Game game = Imperator.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "kick");
		} else if(!game.getOwner().equals(member)) {
			throw new InvalidRequestException("Only the game owner can kick players.", "game", "kick");
		} else if(member.getId() == uid) {
			throw new InvalidRequestException("You cannot kick yourself.", "game", "kick");
		} else if(game.hasStarted() || game.hasEnded()) {
			throw new InvalidRequestException("Cannot kick after starting", "game", "kick");
		}
		Player player = game.getPlayerById(uid);
		if(player != null) {
			try {
				game.removePlayer(player);
			} catch (TransactionException e) {
				throw new RequestException("Failed to remove player", "game", "kick", e);
			}
		}
	}
}
