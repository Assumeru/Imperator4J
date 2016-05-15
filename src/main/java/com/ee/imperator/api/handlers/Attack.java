package com.ee.imperator.api.handlers;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;

@Request(mode = "game", type = "attack")
public class Attack {
	public JSONObject handle(Member member, @Param("gid") int gid, @Param("units") int units, @Param("to") String tid, @Param("from") String fid, @Param("move") int move) throws RequestException {
		Game game = Imperator.getData().getGame(gid);
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", "game", "attack");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", "game", "attack");
		} else if(game.getState() != Game.State.COMBAT && game.getState() != Game.State.TURN_START) {
			throw new InvalidRequestException("Cannot attack now", "game", "attack");
		} else if(units < 1 || units > Game.MAX_ATTACKERS) {
			throw new InvalidRequestException("Invalid amount of attackers", "game", "attack");
		}
		Territory to = game.getMap().getTerritories().get(tid);
		Territory from = game.getMap().getTerritories().get(fid);
		if(to == null || from == null) {
			throw new InvalidRequestException("Territory does not exist", "game", "attack");
		} else if(!from.getOwner().equals(member) || to.getOwner().equals(member) || units >= from.getUnits() || move >= from.getUnits() || !from.getBorders().contains(to)) {
			throw new InvalidRequestException("Invalid attack", "game", "attack");
		}
		for(com.ee.imperator.game.Attack attack : game.getAttacks()) {
			if(attack.getAttacker().equals(to) || attack.getAttacker().equals(from) || attack.getDefender().equals(to) || attack.getDefender().equals(from)) {
				throw new InvalidRequestException(String.valueOf(member.getLanguage().translate("One of these territories is already engaged in combat.")), "game", "attack");
			}
		}
		Imperator.getData().setState(game, Game.State.COMBAT);
		com.ee.imperator.game.Attack attack = new com.ee.imperator.game.Attack(from, to, Math.max(1, move));
		attack.rollAttack(units);
		if(to.getUnits() == 1 || to.getOwner().getAutoRoll() || attack.attackerCannotWin()) {
			attack.autoRollDefence();
			game.executeAttack(attack);
			return new JSONObject()
					.put("territories", new JSONObject()
							.put(to.getId(), new JSONObject()
									.put("uid", to.getOwner().getId())
									.put("units", to.getUnits()))
							.put(from.getId(), new JSONObject()
									.put("uid", from.getOwner().getId())
									.put("units", from.getUnits())))
					.put("state", game.getState().ordinal())
					.put("update", game.getTime())
					.put("attack", getAttackJSON(attack));
		}
		Imperator.getData().saveAttack(game, attack);
		return new JSONObject()
				.put("attacks", game.getAttacks())
				.put("attack", getAttackJSON(attack));
	}

	static JSONArray getAttacks(Game game) {
		JSONArray attacks = new JSONArray();
		for(com.ee.imperator.game.Attack attack : game.getAttacks()) {
			attacks.put(getAttackJSON(attack));
		}
		return attacks;
	}

	static JSONObject getAttackJSON(com.ee.imperator.game.Attack attack) {
		JSONObject out = new JSONObject()
				.put("attacker", attack.getAttacker().getId())
				.put("defender", attack.getDefender().getId())
				.put("attackroll", attack.getAttackRoll())
				.put("move", attack.getMove());
		if(attack.getDefendRoll() != null) {
			out.put("defendroll", attack.getDefendRoll());
		}
		return out;
	}
}
