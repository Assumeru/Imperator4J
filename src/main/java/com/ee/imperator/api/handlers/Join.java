package com.ee.imperator.api.handlers;

import java.util.Arrays;
import java.util.List;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

@Endpoint(mode = Mode.GAME, type = "join")
public class Join {
	private final ImperatorApplicationContext context;

	public Join(ImperatorApplicationContext context) {
		this.context = context;
	}

	public void handle(Member member, @Param("gid") int gid, @Param("color") String color) throws RequestException, TransactionException {
		if(member.isGuest()) {
			throw new InvalidRequestException("Not logged in", Mode.GAME, "join");
		}
		Game game = context.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", Mode.GAME, "join");
		}
		List<String> colors = Arrays.asList(context.getStringsSetting(Player.class, "color.hex"));
		if(!colors.contains(color)) {
			throw new InvalidRequestException("Unknown color", Mode.GAME, "join");
		}
		Player player = new Player(member);
		player.setColor(color);
		handle(member, game, player);
	}

	public void handle(Member member, @Param("game") Game game, @Param("player") Player player) throws TransactionException {
		game.addPlayer(context, player);
	}
}
