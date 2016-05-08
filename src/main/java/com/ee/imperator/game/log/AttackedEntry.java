package com.ee.imperator.game.log;

import java.util.Map;

import org.ee.collection.MapBuilder;

import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public class AttackedEntry extends LogEntry {
	private final Player defender;
	private final int[] attackRoll;
	private final int[] defendRoll;
	private final Territory attacking;
	private final Territory defending;

	public AttackedEntry(long time, Player attacker, Player defender, int[] attackRoll, int[] defendRoll, Territory attacking, Territory defending) {
		super(attacker, time);
		this.defender = defender;
		this.attackRoll = attackRoll;
		this.defendRoll = defendRoll;
		this.attacking = attacking;
		this.defending = defending;
	}

	@Override
	public Map<String, Object> getMessage() {
		return new MapBuilder<String, Object>()
				.put("message", "%1$s vs %2$s: %3$s %4$s")
				.put("attacker", getPlayer().getId())
				.put("defender", defender.getId())
				.put("attacking", attacking.getId())
				.put("defending", defending.getId())
				.put("attackRoll", attackRoll)
				.put("defendRoll", defendRoll).build();
	}

	@Override
	public Type getType() {
		return Type.ATTACKED;
	}

	public Player getDefender() {
		return defender;
	}

	public int[] getAttackRoll() {
		return attackRoll;
	}

	public int[] getDefendRoll() {
		return defendRoll;
	}

	public Territory getAttacking() {
		return attacking;
	}

	public Territory getDefending() {
		return defending;
	}
}
