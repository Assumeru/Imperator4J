package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Cards;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "play-cards")
public class PlayCards {
	public JSONObject handle(Member member, @Param("gid") int gid, @Param("units") int units) throws RequestException {
		Game game = Imperator.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "play-cards");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", "game", "play-cards");
		} else if(game.getState() != Game.State.TURN_START && game.getState() != Game.State.FORTIFY) {
			throw new InvalidRequestException("Cannot play cards after attacking.", "game", "play-cards");
		} else if(!game.getCurrentPlayer().getCards().canPlay(units)) {
			throw new InvalidRequestException("Combination not playable", "game", "play-cards");
		}
		Imperator.getState().playCards(game.getCurrentPlayer(), units);
		return new JSONObject()
				.put("update", game.getTime())
				.put("units", game.getUnits())
				.put("cards", getCards(game.getCurrentPlayer().getCards()));
	}

	static JSONObject getCards(Cards cards) {
		return new JSONObject()
				.put(String.valueOf(Cards.Card.ARTILLERY.ordinal()), cards.getArtillery())
				.put(String.valueOf(Cards.Card.INFANTRY.ordinal()), cards.getInfantry())
				.put(String.valueOf(Cards.Card.CAVALRY.ordinal()), cards.getCavalry())
				.put(String.valueOf(Cards.Card.JOKER.ordinal()), cards.getJokers());
	}
}
