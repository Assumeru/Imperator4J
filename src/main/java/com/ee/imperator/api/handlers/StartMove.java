package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "start-move")
public class StartMove {
	public JSONObject handle(Member member, @Param("gid") int gid) throws RequestException {
		Game game = Imperator.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "start-move");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", "game", "start-move");
		} else if(game.getState() != Game.State.COMBAT) {
			throw new InvalidRequestException("Cannot move before attacking", "game", "start-move");
		} else if(!game.getAttacks().isEmpty()) {
			throw new InvalidRequestException(String.valueOf(member.getLanguage().translate("All battles need to finish before units can be moved.")), "game", "start-move");
		}
		try(GameTransaction transaction = Imperator.getState().modify(game)) {
			transaction.setState(Game.State.POST_COMBAT);
			transaction.setUnits(Game.MAX_MOVE_UNITS);
			transaction.commit();
		} catch (TransactionException e) {
			throw new RequestException("Failed to start moving", "game", "start-move", e);
		}
		return new JSONObject()
				.put("state", game.getState().ordinal())
				.put("units", game.getUnits());
	}
}
