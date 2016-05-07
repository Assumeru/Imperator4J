package com.ee.imperator.game.log;

import org.ee.i18n.Language;

import com.ee.imperator.user.Player;

public abstract class LogEntry {
	public enum Type {
		CONQUERED, ATTACKED, ENDED_TURN, FORFEITED, CARDS_PLAYED
	}
	private final Player player;
	private final long time;

	public LogEntry(Player player, long time) {
		this.player = player;
		this.time = time;
	}

	public Player getPlayer() {
		return player;
	}

	public long getTime() {
		return time;
	}

	public abstract CharSequence getMessage(Language language);

	public abstract Type getType();
}
