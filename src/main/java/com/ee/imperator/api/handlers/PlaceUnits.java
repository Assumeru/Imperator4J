package com.ee.imperator.api.handlers;

import org.ee.i18n.Language;
import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;

@Endpoint(mode = Mode.GAME, type = "place-units")
public class PlaceUnits {
	private final ImperatorApplicationContext context;

	public PlaceUnits(ImperatorApplicationContext context) {
		this.context = context;
	}

	public JSONObject handle(Member member, @Param("gid") int gid, @Param("units") int units, @Param("territory") String tid) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		checkParams(game, member, units);
		Territory territory = game.getMap().getTerritories().get(tid);
		if(territory == null) {
			throw new InvalidRequestException("Territory does not exist", Mode.GAME, "place-units");
		} else if(!territory.getOwner().equals(member)) {
			throw new InvalidRequestException("Not your territory", Mode.GAME, "place-units");
		}
		units = Math.max(1, units);
		try(GameTransaction transaction = context.getState().modify(game)) {
			transaction.setTime(System.currentTimeMillis());
			transaction.setUnits(game.getUnits() - units);
			transaction.getTerritory(territory).setUnits(territory.getUnits() + units);
			transaction.commit();
		}
		return new JSONObject()
				.put("update", game.getTime())
				.put("units", game.getUnits())
				.put("territories", new JSONObject()
						.put(territory.getId(), new JSONObject()
								.put("units", territory.getUnits())));
	}

	private void checkParams(Game game, Member member, int units) throws InvalidRequestException {
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", Mode.GAME, "place-units");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", Mode.GAME, "place-units");
		} else if(game.getState() != Game.State.TURN_START && game.getState() != Game.State.FORTIFY) {
			throw new InvalidRequestException("Cannot place units after attacking.", Mode.GAME, "place-units");
		} else if(game.getUnits() < units) {
			Language i18n = member.getLanguage();
			throw new InvalidRequestException(String.valueOf(i18n.__(i18n.resolve("Cannot place more than one unit.", "Cannot place more than %1$d units.", game.getUnits()), game.getUnits())), Mode.GAME, "place-units");
		}
	}
}
