package com.ee.imperator.game;

import com.ee.imperator.map.Territory;

public class Attack {
	private final Territory attacker;
	private final Territory defender;
	private final int move;
	private int[] attackRoll;
	private int[] defendRoll;
	private int attackLosses;
	private int defendLosses;

	public Attack(Territory attacker, Territory defender, int move) {
		this(attacker, defender, move, null);
	}

	public Attack(Territory attacker, Territory defender, int move, int[] attackRoll) {
		this.attacker = attacker;
		this.defender = defender;
		this.move = move;
		this.attackRoll = attackRoll;
	}

	public Territory getAttacker() {
		return attacker;
	}

	public Territory getDefender() {
		return defender;
	}

	public int[] getAttackRoll() {
		return attackRoll;
	}

	public int[] getDefendRoll() {
		return defendRoll;
	}

	public int getAttackLosses() {
		return attackLosses;
	}

	public int getDefendLosses() {
		return defendLosses;
	}

	public int getMove() {
		return move;
	}

	private int[] rollDice(int number) {
		int[] roll = new int[number];
		for(int i = 0; i < roll.length; i++) {
			roll[i] = (int) (Math.random() * 6) + 1;
			for(int n = i - 1; n >= 0; n--) {
				int j = n + 1;
				if(roll[n] < roll[j]) {
					int v = roll[n];
					roll[n] = roll[j];
					roll[j] = v;
				}
			}
		}
		return roll;
	}

	public void rollAttack(int dice) {
		attackRoll = rollDice(dice);
	}

	public void rollDefence(int dice) {
		defendRoll = rollDice(dice);
		calculateLosses();
	}

	public boolean attackerCannotWin() {
		return attackRoll.length == 1 && attackRoll[0] == 1;
	}

	public void autoRollDefence() {
		int dice = 1;
		if(defender.getUnits() > 1 && (attackRoll.length == 1 || attackRoll[0] + attackRoll[1] <= 7)) {
			dice++;
		}
		rollDefence(dice);
	}

	private void calculateLosses() {
		attackLosses = 0;
		defendLosses = 0;
		for(int i = 0; i < attackRoll.length && i < defendRoll.length; i++) {
			if(attackRoll[i] > defendRoll[i]) {
				defendLosses++;
			} else {
				attackLosses++;
			}
		}
	}
}
