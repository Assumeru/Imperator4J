package com.ee.imperator.game.log;

import org.ee.i18n.Language;

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
	public CharSequence getMessage(Language language) {
		//TODO
		return language.translate("%1$s vs %2$s: %3$s %4$s");
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
