package com.ee.imperator.api.handlers;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "forfeit")
public class Forfeit {
	private final ImperatorApplicationContext context;

	public Forfeit(ImperatorApplicationContext context) {
		this.context = context;
	}

	public void handle(Member member, @Param("gid") int gid) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		checkParams(game, member);
		game.forfeit(context, game.getPlayerById(member.getId()));
	}

	private void checkParams(Game game, Member member) throws InvalidRequestException {
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "forfeit");
		} else if(!game.getPlayers().contains(member)) {
			throw new InvalidRequestException("Not a player", "game", "forfeit");
		} else if(game.getState() == Game.State.COMBAT) {
			synchronized(game.getAttacks()) {
				for(Attack attack : game.getAttacks()) {
					if(attack.getDefender().getOwner().equals(member)) {
						throw new InvalidRequestException(String.valueOf(member.getLanguage().translate("You cannot forfeit without finishing all battles.")), "game", "forfeit");
					}
				}
			}
		}
	}
}
