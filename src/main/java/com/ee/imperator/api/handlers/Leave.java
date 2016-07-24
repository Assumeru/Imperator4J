package com.ee.imperator.api.handlers;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

@Request(mode = "game", type = "leave")
public class Leave {
	public void handle(Member member, @Param("gid") int gid) throws RequestException, TransactionException {
		Game game = Imperator.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "leave");
		}
		Player player = game.getPlayerById(member.getId());
		if(player == null) {
			throw new InvalidRequestException("Not a player", "game", "leave");
		}
		handle(member, game, player);
	}

	public void handle(Member member, @Param("game") Game game, @Param("player") Player player) throws RequestException, TransactionException {
		if(game.getOwner().equals(player)) {
			if(game.hasStarted() || game.hasEnded()) {
				throw new InvalidRequestException("Cannot delete game after starting", "game", "leave");
			}
			Imperator.getState().deleteGame(game);
		} else {
			game.removePlayer(player);
		}
	}
}
