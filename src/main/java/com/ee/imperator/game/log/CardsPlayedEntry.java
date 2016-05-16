package com.ee.imperator.game.log;

import java.util.Map;

import org.ee.collection.MapBuilder;

import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.user.Player;

public class CardsPlayedEntry extends LogEntry {
	private final int[] cards;
	private final int units;

	public CardsPlayedEntry(Player player, long time, int[] cards, int units) {
		super(player, time);
		this.cards = cards;
		this.units = units;
	}

	public CardsPlayedEntry(Player player, long time, Card[] cards, int units) {
		this(player, time, getOrdinals(cards), units);
	}

	private static int[] getOrdinals(Card[] cards) {
		int[] out = new int[cards.length];
		for(int i = 0; i < cards.length; i++) {
			out[i] = cards[i].ordinal();
		}
		return out;
	}

	public int[] getCards() {
		return cards;
	}

	public int getUnits() {
		return units;
	}

	@Override
	public Map<String, Object> getMessage() {
		return new MapBuilder<String, Object>()
				.put("message", "%1$s played %2$s for %3$d units.")
				.put("uid", getPlayer().getId())
				.put("cards", cards)
				.put("units", units)
				.build();
	}

	@Override
	public Type getType() {
		return Type.CARDS_PLAYED;
	}
}
