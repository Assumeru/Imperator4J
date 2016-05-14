package com.ee.imperator.api.handlers;

import org.ee.i18n.Language;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "place-units")
public class PlaceUnits {
	public JSONObject handle(Member member, @Param("gid") int gid, @Param("units") int units, @Param("territory") String tid) throws RequestException {
		Game game = Imperator.getData().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "place-units");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", "game", "place-units");
		} else if(game.getState() != Game.State.TURN_START && game.getState() != Game.State.FORTIFY) {
			throw new InvalidRequestException("Cannot place units after attacking.", "game", "place-units");
		} else if(game.getUnits() < units) {
			Language i18n = member.getLanguage();
			throw new InvalidRequestException(String.valueOf(i18n.__(i18n.resolve("Cannot place more than one unit.", "Cannot place more than %1$d units.", game.getUnits()), game.getUnits())), "game", "place-units");
		}
		Territory territory = game.getMap().getTerritories().get(tid);
		if(territory == null) {
			throw new InvalidRequestException("Territory does not exist", "game", "place-units");
		} else if(!territory.getOwner().equals(member)) {
			throw new InvalidRequestException("Not your territory", "game", "place-units");
		}
		Imperator.getData().placeUnits(game, territory, units);
		return new JSONObject()
				.put("update", game.getTime())
				.put("units", game.getUnits())
				.put("territories", new JSONObject()
						.put(territory.getId(), new JSONObject()
								.put("units", territory.getUnits())));
	}
}