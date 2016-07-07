package com.ee.imperator.data.transaction;

import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Cards;
import com.ee.imperator.mission.PlayerMission;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.Player.State;

public abstract class AbstractPlayerTransaction implements PlayerTransaction {
	public enum Score {
		WIN, LOSS
	}
	protected final Player player;
	private final Cards cards;
	private State state;
	private PlayerMission mission;
	private boolean autoroll;
	private Score score;

	public AbstractPlayerTransaction(Player player) {
		this.player = player;
		cards = new Cards();
		setValues();
	}

	private void setValues() {
		state = player.getState();
		mission = player.getMission();
		autoroll = player.getAutoRoll();
		cards.setArtillery(player.getCards().getArtillery());
		cards.setCavalry(player.getCards().getCavalry());
		cards.setInfantry(player.getCards().getInfantry());
		cards.setJokers(player.getCards().getJokers());
		score = null;
	}

	@Override
	public void revert() throws TransactionException {
		setValues();
	}

	@Override
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public void setMission(PlayerMission mission) {
		this.mission = mission;
	}

	@Override
	public void setAutoRoll(boolean autoroll) {
		this.autoroll = autoroll;
	}

	@Override
	public void addLoss() {
		score = Score.LOSS;
	}

	@Override
	public void addWin() {
		score = Score.WIN;
	}

	protected State getState() {
		return state;
	}

	protected PlayerMission getMission() {
		return mission;
	}

	protected boolean getAutoroll() {
		return autoroll;
	}

	@Override
	public Cards getCards() {
		return cards;
	}

	protected Score getScore() {
		return score;
	}

	/**
	 * Writes changes to underlying player.
	 */
	protected void apply() {
		player.setAutoRoll(autoroll);
		player.setMission(mission);
		player.setState(state);
		player.getCards().setArtillery(cards.getArtillery());
		player.getCards().setCavalry(cards.getCavalry());
		player.getCards().setInfantry(cards.getInfantry());
		player.getCards().setJokers(cards.getJokers());
		Member member = player.getMember();
		if(score == Score.WIN) {
			member.setScore(member.getScore() + player.getGame().getPlayers().size() - 1);
			member.setWins(member.getWins() + 1);
		} else if(score == Score.LOSS) {
			member.setScore(member.getScore() - 1);
			member.setLosses(member.getLosses() + 1);
		}
	}
}
