package com.ee.imperator.api.handlers;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

@Endpoint(mode = Mode.GAME, type = "leave")
public class Leave {
	private final ImperatorApplicationContext context;

	public Leave(ImperatorApplicationContext context) {
		this.context = context;
	}

	public void handle(Member member, @Param("gid") int gid) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", Mode.GAME, "leave");
		}
		Player player = game.getPlayerById(member.getId());
		if(player == null) {
			throw new InvalidRequestException("Not a player", Mode.GAME, "leave");
		}
		handle(member, game, player);
	}

	public void handle(Member member, @Param("game") Game game, @Param("player") Player player) throws RequestException, TransactionException {
		if(game.getOwner().equals(player)) {
			if(game.hasStarted() || game.hasEnded()) {
				throw new InvalidRequestException("Cannot delete game after starting", Mode.GAME, "leave");
			}
			context.getState().deleteGame(game);
		} else {
			game.removePlayer(context, player);
		}
	}
}
