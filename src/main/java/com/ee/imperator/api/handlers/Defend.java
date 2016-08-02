package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;

@Endpoint(mode = Mode.GAME, type = "defend")
public class Defend {
	private final ImperatorApplicationContext context;

	public Defend(ImperatorApplicationContext context) {
		this.context = context;
	}

	public JSONObject handle(Member member, @Param("gid") int gid, @Param("to") String tid, @Param("from") String fid, @Param("units") int units) throws RequestException, TransactionException {
		if(units < 1 || units > Game.MAX_DEFENDERS) {
			throw new InvalidRequestException("Invalid number of defenders", Mode.GAME, "defend");
		}
		Game game = context.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", Mode.GAME, "defend");
		}
		Territory to = game.getMap().getTerritories().get(tid);
		Territory from = game.getMap().getTerritories().get(fid);
		if(to == null || from == null) {
			throw new InvalidRequestException("Territory not found", Mode.GAME, "defend");
		} else if(!to.getOwner().equals(member)) {
			throw new InvalidRequestException("Not your territory", Mode.GAME, "defend");
		}
		com.ee.imperator.game.Attack attack = getAttack(game, to, from);
		game.defend(context, attack, units);
		return Attack.getAttackResponse(game, to, from, attack);
	}

	private com.ee.imperator.game.Attack getAttack(Game game, Territory to, Territory from) throws RequestException {
		synchronized(game.getAttacks()) {
			for(com.ee.imperator.game.Attack attack : game.getAttacks()) {
				if(attack.getAttacker().equals(from) && attack.getDefender().equals(to)) {
					return attack;
				}
			}
		}
		throw new InvalidRequestException("Attack not found", Mode.GAME, "defend");
	}
}
