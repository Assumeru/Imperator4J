package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

@Request(mode = "game", type = "autoroll")
public class SetAutoRoll {
	private final ImperatorApplicationContext context;

	public SetAutoRoll(ImperatorApplicationContext context) {
		this.context = context;
	}

	public JSONObject handle(Member member, @Param("gid") int gid, @Param("autoroll") boolean autoroll) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "autoroll");
		}
		Player player = game.getPlayerById(member.getId());
		if(player == null) {
			throw new InvalidRequestException("Player not in game", "game", "autoroll");
		}
		try(GameTransaction transaction = context.getState().modify(game)) {
			transaction.getPlayer(player).setAutoRoll(autoroll);
			transaction.commit();
		}
		return new JSONObject().put("autoroll", player.getAutoRoll());
	}
}
