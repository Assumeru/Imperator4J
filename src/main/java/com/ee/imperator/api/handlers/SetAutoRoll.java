package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

@Request(mode = "game", type = "autoroll")
public class SetAutoRoll {
	public JSONObject handle(Member member, @Param("gid") int gid, @Param("autoroll") boolean autoroll) throws RequestException {
		Game game = Imperator.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "autoroll");
		}
		Player player = game.getPlayerById(member.getId());
		if(player == null) {
			throw new InvalidRequestException("Player not in game", "game", "autoroll");
		}
		Imperator.getState().setAutoRoll(player, autoroll);
		return new JSONObject().put("autoroll", player.getAutoRoll());
	}
}
