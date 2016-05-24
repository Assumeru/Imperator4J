package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "defend")
public class Defend {
	public JSONObject handle(Member member, @Param("gid") int gid, @Param("to") String tid, @Param("from") String fid, @Param("units") int units) throws RequestException {
		if(units < 1 || units > Game.MAX_DEFENDERS) {
			throw new InvalidRequestException("Invalid number of defenders", "game", "defend");
		}
		Game game = Imperator.getState().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "defend");
		}
		Territory to = game.getMap().getTerritories().get(tid);
		Territory from = game.getMap().getTerritories().get(fid);
		if(to == null || from == null) {
			throw new InvalidRequestException("Territory not found", "game", "defend");
		} else if(!to.getOwner().equals(member)) {
			throw new InvalidRequestException("Not your territory", "game", "defend");
		}
		com.ee.imperator.game.Attack attack = getAttack(game, to, from);
		attack.rollDefence(units);
		game.executeAttack(attack);
		Imperator.getState().deleteAttack(attack);
		return Attack.getAttackResponse(game, to, from, attack);
	}

	private com.ee.imperator.game.Attack getAttack(Game game, Territory to, Territory from) throws RequestException {
		for(com.ee.imperator.game.Attack attack : game.getAttacks()) {
			if(attack.getAttacker().equals(from) && attack.getDefender().equals(to)) {
				return attack;
			}
		}
		throw new InvalidRequestException("Attack not found", "game", "defend");
	}
}
