package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Endpoint(mode = Mode.GAME, type = "fortify")
public class Fortify {
	private final ImperatorApplicationContext context;

	public Fortify(ImperatorApplicationContext context) {
		this.context = context;
	}

	public JSONObject handle(Member member, @Param("gid") int gid) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", Mode.GAME, "fortify");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", Mode.GAME, "fortify");
		} else if(game.getState() != Game.State.TURN_START) {
			throw new InvalidRequestException("Cannot fortify after attacking.", Mode.GAME, "fortify");
		}
		try(GameTransaction transaction = context.getState().modify(game)) {
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
