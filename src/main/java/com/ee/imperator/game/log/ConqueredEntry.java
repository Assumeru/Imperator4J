package com.ee.imperator.game.log;

import java.util.Map;

import org.ee.collection.MapBuilder;

import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public class ConqueredEntry extends LogEntry {
	private final Territory territory;

	public ConqueredEntry(Player player, long time, Territory territory) {
		super(player, time);
		this.territory = territory;
	}

	@Override
	public Map<String, Object> getMessage() {
		return new MapBuilder<String, Object>()
				.put("message", "%1$s conquered %2$s.")
				.put("uid", getPlayer().getId())
				.put("territory", territory.getId())
				.build();
	}

	@Override
	public Type getType() {
		return Type.CONQUERED;
	}
}
