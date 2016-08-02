package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Cards;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Endpoint(mode = Mode.GAME, type = "end-turn")
public class EndTurn {
	private final ImperatorApplicationContext context;

	public EndTurn(ImperatorApplicationContext context) {
		this.context = context;
	}

	public JSONObject handle(Member member, @Param("gid") int gid, @Param("card") int cid) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		checkParams(game, member);
		JSONObject out = new JSONObject();
		boolean conquered = game.hasConquered();
		Cards.Card card = game.endTurn(context, Cards.Card.valueOf(cid));
		if(conquered) {
			out.put("card", card == null ? -1 : card.ordinal());
		}
		out.put("turn", game.getCurrentPlayer() == null ? 0 : game.getCurrentPlayer().getId())
				.put("update", game.getTime())
				.put("state", game.getState().ordinal());
		return out;
	}

	private void checkParams(Game game, Member member) throws InvalidRequestException {
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", Mode.GAME, "end-turn");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", Mode.GAME, "end-turn");
		} else if(game.getState() == Game.State.COMBAT && !game.getAttacks().isEmpty()) {
			throw new InvalidRequestException(String.valueOf(member.getLanguage().translate("You cannot end your turn without finishing all battles.")), Mode.GAME, "end-turn");
		}
	}
}
