package com.ee.imperator.game.log;

import java.util.Map;

import org.ee.collection.MapBuilder;

import com.ee.imperator.user.Player;

public class EndedTurnEntry extends LogEntry {
	public EndedTurnEntry(Player player, long time) {
		super(player, time);
	}

	@Override
	public Type getType() {
		return Type.ENDED_TURN;
	}

	@Override
	public Map<String, Object> getMessage() {
		return new MapBuilder<String, Object>()
				.put("message", "%1$s's turn has ended.")
				.put("uid", getPlayer().getId())
				.build();
	}
}
