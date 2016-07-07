package com.ee.imperator.data.transaction;

import com.ee.imperator.game.Cards;
import com.ee.imperator.mission.PlayerMission;
import com.ee.imperator.user.Player.State;

public interface PlayerTransaction extends Revertible {
	void setState(State state);

	void setMission(PlayerMission mission);

	void setAutoRoll(boolean autoroll);

	Cards getCards();

	void addWin();

	void addLoss();
}
