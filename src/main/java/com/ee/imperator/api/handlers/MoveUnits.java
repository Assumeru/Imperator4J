package com.ee.imperator.api.handlers;

import org.ee.i18n.Language;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "move")
public class MoveUnits {
	public JSONObject handle(Member member, @Param("gid") int gid, @Param("to") String tid, @Param("from") String fid, @Param("move") int move) throws RequestException {
		Game game = Imperator.getState().getGame(gid);
		checkParameters(game, member, move);
		Territory to = game.getMap().getTerritories().get(tid);
		Territory from = game.getMap().getTerritories().get(fid);
		if(to == null || from == null) {
			throw new InvalidRequestException("Territory does not exist", "game", "move");
		} else if(!from.getOwner().equals(member) || !to.getOwner().equals(member) || move >= from.getUnits() || move < 1 || !from.getBorders().contains(to)) {
			throw new InvalidRequestException("Invalid move", "game", "move");
		}
		try {
			game.moveUnits(from, to, move);
		} catch (TransactionException e) {
			throw new RequestException("Failed to move units", "game", "move", e);
		}
		return new JSONObject()
				.put("territories", new JSONObject()
						.put(to.getId(), new JSONObject()
								.put("units", to.getUnits()))
						.put(from.getId(), new JSONObject()
								.put("units", from.getUnits())))
				.put("update", game.getTime())
				.put("units", game.getUnits());
	}

	private void checkParameters(Game game, Member member, int move) throws InvalidRequestException {
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "move");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", "game", "move");
		} else if(game.getState() != Game.State.POST_COMBAT) {
			throw new InvalidRequestException("Cannot move now", "game", "move");
		} else if(game.getUnits() < move) {
			Language i18n = member.getLanguage();
			throw new InvalidRequestException(String.valueOf(i18n.__(i18n.resolve("Cannot move more than one unit.", "Cannot move more than %1$d units.", game.getUnits()), game.getUnits())), "game", "move");
		}
	}
}
