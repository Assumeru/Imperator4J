package com.ee.imperator.game.log;

import java.util.Map;

import org.ee.collection.MapBuilder;

import com.ee.imperator.user.Player;

public class ForfeitedEntry extends LogEntry {
	public ForfeitedEntry(Player player, long time) {
		super(player, time);
	}

	@Override
	public Map<String, Object> getMessage() {
		return new MapBuilder<String, Object>()
				.put("message", "%1$s has forfeited the game.")
				.put("uid", getPlayer().getId())
				.build();
	}

	@Override
	public Type getType() {
		return Type.FORFEITED;
	}
}
