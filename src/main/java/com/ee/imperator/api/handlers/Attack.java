package com.ee.imperator.api.handlers;

import org.json.JSONArray;
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

@Endpoint(mode = Mode.GAME, type = "attack")
public class Attack {
	private final ImperatorApplicationContext context;

	public Attack(ImperatorApplicationContext context) {
		this.context = context;
	}

	public JSONObject handle(Member member, @Param("gid") int gid, @Param("units") int units, @Param("to") String tid, @Param("from") String fid, @Param("move") int move) throws RequestException, TransactionException {
		Game game = context.getState().getGame(gid);
		checkParams(game, member, units);
		Territory to = game.getMap().getTerritories().get(tid);
		Territory from = game.getMap().getTerritories().get(fid);
		if(to == null || from == null) {
			throw new InvalidRequestException("Territory does not exist", Mode.GAME, "attack");
		} else if(!from.getOwner().equals(member) || to.getOwner().equals(member) || units >= from.getUnits() || move >= from.getUnits() || !from.getBorders().contains(to)) {
			throw new InvalidRequestException("Invalid attack", Mode.GAME, "attack");
		}
		com.ee.imperator.game.Attack attack;
		boolean autoRoll;
		synchronized(game.getAttacks()) {
			for(com.ee.imperator.game.Attack a : game.getAttacks()) {
				if(a.getAttacker().equals(to) || a.getAttacker().equals(from) || a.getDefender().equals(to) || a.getDefender().equals(from)) {
					throw new InvalidRequestException(String.valueOf(member.getLanguage().translate("One of these territories is already engaged in combat.")), Mode.GAME, "attack");
				}
			}
			attack = new com.ee.imperator.game.Attack(from, to, Math.max(1, move));
			attack.rollAttack(units);
			autoRoll = to.getUnits() == 1 || to.getOwner().getAutoRoll() || attack.attackerCannotWin();
			try(GameTransaction transaction = context.getState().modify(game)) {
				transaction.setState(Game.State.COMBAT);
				if(autoRoll) {
					attack.autoRollDefence();
					game.executeAttack(attack, transaction);
				} else {
					transaction.setTime(System.currentTimeMillis());
					transaction.getAttacks().add(attack);
				}
				transaction.commit();
			}
		}
		if(autoRoll) {
			return getAttackResponse(game, to, from, attack);
		}
		return new JSONObject()
				.put("attacks", getAttacks(game))
				.put("attack", getAttackJSON(attack));
	}

	private void checkParams(Game game, Member member, int units) throws InvalidRequestException {
		if(game == null) {
			throw new InvalidRequestException("Game does not exist", Mode.GAME, "attack");
		} else if(!game.getCurrentPlayer().equals(member)) {
			throw new InvalidRequestException("Not your turn", Mode.GAME, "attack");
		} else if(game.getState() != Game.State.COMBAT && game.getState() != Game.State.TURN_START) {
			throw new InvalidRequestException("Cannot attack now", Mode.GAME, "attack");
		} else if(units < 1 || units > Game.MAX_ATTACKERS) {
			throw new InvalidRequestException("Invalid amount of attackers", Mode.GAME, "attack");
		}
	}

	static JSONObject getAttackResponse(Game game, Territory to, Territory from, com.ee.imperator.game.Attack attack) {
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

	static JSONArray getAttacks(Game game) {
		JSONArray attacks = new JSONArray();
		synchronized(game.getAttacks()) {
			for(com.ee.imperator.game.Attack attack : game.getAttacks()) {
				attacks.put(getAttackJSON(attack));
			}
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
