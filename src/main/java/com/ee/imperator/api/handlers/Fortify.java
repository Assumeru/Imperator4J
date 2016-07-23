package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "fortify")
public class Fortify {
	public JSONObject handle(Member member, @Param("gid") int gid) throws RequestException, TransactionException {
		Game game = Imperator.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "fortify");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", "game", "fortify");
		} else if(game.getState() != Game.State.TURN_START) {
			throw new InvalidRequestException("Cannot fortify after attacking.", "game", "fortify");
		}
		try(GameTransaction transaction = Imperator.getState().modify(game)) {
			transaction.setState(Game.State.FORTIFY);
			int units = game.getPlayerById(member.getId()).getUnitsFromTerritoriesPerTurn();
			transaction.setUnits(game.getUnits() + units);
			transaction.commit();
		}
		return new JSONObject()
				.put("units", game.getUnits())
				.put("state", game.getState().ordinal())
				.put("update", game.getTime());
	}
}
