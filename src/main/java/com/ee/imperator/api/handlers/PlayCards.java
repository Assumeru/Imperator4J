package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Cards;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.CardsPlayedEntry;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

@Request(mode = "game", type = "play-cards")
public class PlayCards {
	private final ImperatorApplicationContext context;

	public PlayCards(ImperatorApplicationContext context) {
		this.context = context;
	}

	public JSONObject handle(Member member, @Param("gid") int gid, @Param("units") int units) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "play-cards");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", "game", "play-cards");
		} else if(game.getState() != Game.State.TURN_START && game.getState() != Game.State.FORTIFY) {
			throw new InvalidRequestException("Cannot play cards after attacking.", "game", "play-cards");
		} else if(!game.getCurrentPlayer().getCards().canPlay(units)) {
			throw new InvalidRequestException("Combination not playable", "game", "play-cards");
		}
		try(GameTransaction transaction = context.getState().modify(game)) {
			Player player = game.getCurrentPlayer();
			Cards combo = player.getCards().getCombination(units);
			long time = System.currentTimeMillis();
			transaction.setUnits(game.getUnits() + units);
			transaction.setTime(time);
			transaction.addEntry(new CardsPlayedEntry(player, time, combo.toArray(), units));
			transaction.getPlayer(player).getCards().removeAll(combo);
			transaction.commit();
		}
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
