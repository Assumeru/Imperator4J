package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Cards;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "end-turn")
public class EndTurn {
	public JSONObject handle(Member member, @Param("gid") int gid, @Param("card") int cid) throws RequestException {
		Game game = Imperator.getData().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "end-turn");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", "game", "end-turn");
		} else if(game.getState() == Game.State.COMBAT && !game.getAttacks().isEmpty()) {
			throw new InvalidRequestException(String.valueOf(member.getLanguage().translate("You cannot end your turn without finishing all battles.")), "game", "end-turn");
		}
		JSONObject out = new JSONObject();
		if(game.hasConquered()) {
			Cards.Card card = game.giveCard(game.getPlayerById(member.getId()), Cards.Card.valueOf(cid));
			out.put("card", card == null ? -1 : card.ordinal());
		}
		game.nextTurn();
		out.put("turn", game.getCurrentPlayer().getId())
				.put("update", game.getTime())
				.put("state", game.getState().ordinal());
		return out;
	}
}