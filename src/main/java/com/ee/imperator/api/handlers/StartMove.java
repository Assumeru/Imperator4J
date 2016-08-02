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

@Endpoint(mode = Mode.GAME, type = "start-move")
public class StartMove {
	private final ImperatorApplicationContext context;

	public StartMove(ImperatorApplicationContext context) {
		this.context = context;
	}

	public JSONObject handle(Member member, @Param("gid") int gid) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", Mode.GAME, "start-move");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", Mode.GAME, "start-move");
		} else if(game.getState() != Game.State.COMBAT) {
			throw new InvalidRequestException("Cannot move before attacking", Mode.GAME, "start-move");
		} else if(!game.getAttacks().isEmpty()) {
			throw new InvalidRequestException(String.valueOf(member.getLanguage().translate("All battles need to finish before units can be moved.")), Mode.GAME, "start-move");
		}
		try(GameTransaction transaction = context.getState().modify(game)) {
			transaction.setState(Game.State.POST_COMBAT);
			transaction.setUnits(Game.MAX_MOVE_UNITS);
			transaction.commit();
		}
		return new JSONObject()
				.put("state", game.getState().ordinal())
				.put("units", game.getUnits());
	}
}
