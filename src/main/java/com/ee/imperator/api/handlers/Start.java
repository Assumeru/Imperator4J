package com.ee.imperator.api.handlers;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "start")
public class Start {
	private final ImperatorApplicationContext context;

	public Start(ImperatorApplicationContext context) {
		this.context = context;
	}

	public void handle(Member member, @Param("gid") int gid) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "start");
		}
		handle(member, game);
	}

	public void handle(Member member, @Param("game") Game game) throws RequestException, TransactionException {
		if(!game.getOwner().equals(member) || game.getPlayers().size() != game.getMap().getPlayers()) {
			throw new InvalidRequestException("Cannot start game", "game", "start");
		}
		game.start(context);
	}
}
