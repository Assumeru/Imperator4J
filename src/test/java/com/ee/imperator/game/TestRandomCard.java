package com.ee.imperator.game;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ee.imperator.game.Cards.Card;

public class TestRandomCard {
	@Test
	public void test() {
		Cards cards = new Cards();
		Map<Card, Integer> num = new HashMap<>();
		for(int i = 0; i < 2000; i++) {
			Card c = Card.getRandom(cards);
			add(num, c);
			cards.add(c);
		}
		Assert.assertTrue(get(num, Card.JOKER) <= Cards.MAX_JOKERS);
	}

	private int get(Map<Card, Integer> map, Card key) {
		Integer value = map.get(key);
		if(value == null) {
			return 0;
		}
		return value;
	}

	private void add(Map<Card, Integer> map, Card key) {
		map.put(key, get(map, key) + 1);
	}
}
