package com.ee.imperator.game;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.ee.collection.ArrayIterator;

import com.ee.imperator.game.Cards.Card;

public class Cards extends AbstractCollection<Card> {
	public enum Card {
		ARTILLERY(14 / 44d, "Artillery"), CAVALRY(14 / 44d, "Cavalry"), INFANTRY(14 / 44d, "Infantry"), JOKER(1 / 22d, "Joker");
		private double chance;
		private String name;

		private Card(double chance, String name) {
			this.chance = chance;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Card valueOf(int ordinal) {
			if(ordinal < 0 || ordinal >= values().length) {
				return null;
			}
			return values()[ordinal];
		}

		public static Card getRandom(Cards cards) {
			Random random = new Random();
			double r = random.nextDouble();
			double p = 0;
			for(int i = 0; i < Card.values().length - 1; i++) {
				p += Card.values()[i].chance;
				if(r < p) {
					return Card.values()[i];
				}
			}
			if(cards.getJokers() < Cards.MAX_JOKERS) {
				return Card.JOKER;
			}
			return Card.values()[random.nextInt(3)];
		}
	}
	public static final int MAX_CARDS = 5;
	public static final int MAX_JOKERS = 2;
	private int artillery;
	private int cavalry;
	private int infantry;
	private int jokers;

	public Cards() {
	}

	public Cards(int artillery, int cavalry, int infantry, int jokers) {
		this.artillery = artillery;
		this.cavalry = cavalry;
		this.infantry = infantry;
		this.jokers = jokers;
	}

	public int getArtillery() {
		return artillery;
	}

	public void setArtillery(int artillery) {
		this.artillery = artillery;
	}

	public int getCavalry() {
		return cavalry;
	}

	public void setCavalry(int cavalry) {
		this.cavalry = cavalry;
	}

	public int getInfantry() {
		return infantry;
	}

	public void setInfantry(int infantry) {
		this.infantry = infantry;
	}

	public int getJokers() {
		return jokers;
	}

	public void setJokers(int jokers) {
		this.jokers = jokers;
	}

	@Override
	public int size() {
		return artillery + cavalry + infantry + jokers;
	}

	@Override
	public boolean contains(Object o) {
		if(o == Card.ARTILLERY) {
			return artillery > 0;
		} else if(o == Card.CAVALRY) {
			return cavalry > 0;
		} else if(o == Card.INFANTRY) {
			return infantry > 0;
		} else if(o == Card.JOKER) {
			return jokers > 0;
		}
		return false;
	}

	@Override
	public Iterator<Card> iterator() {
		return new ArrayIterator<>(toArray());
	}

	@Override
	public Card[] toArray() {
		Card[] cards = new Card[size()];
		int index = 0;
		for(int i = 0; index < cards.length && i < artillery; i++, index++) {
			cards[index] = Card.ARTILLERY;
		}
		for(int i = 0; index < cards.length && i < infantry; i++, index++) {
			cards[index] = Card.INFANTRY;
		}
		for(int i = 0; index < cards.length && i < cavalry; i++, index++) {
			cards[index] = Card.CAVALRY;
		}
		for(int i = 0; index < cards.length && i < jokers; i++, index++) {
			cards[index] = Card.JOKER;
		}
		return cards;
	}

	@Override
	public boolean add(Card e) {
		if(e == Card.ARTILLERY) {
			artillery++;
		} else if(e == Card.CAVALRY) {
			cavalry++;
		} else if(e == Card.INFANTRY) {
			infantry++;
		} else if(e == Card.JOKER) {
			jokers++;
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if(o == Card.ARTILLERY) {
			if(artillery > 0) {
				artillery--;
				return true;
			}
		} else if(o == Card.CAVALRY) {
			if(cavalry > 0) {
				cavalry--;
				return true;
			}
		} else if(o == Card.INFANTRY) {
			if(infantry > 0) {
				infantry--;
				return true;
			}
		} else if(o == Card.JOKER) {
			if(jokers > 0) {
				jokers--;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for(Object o : c) {
			remove(o);
		}
		return changed;
	}

	@Override
	public void clear() {
		artillery = 0;
		cavalry = 0;
		infantry = 0;
		jokers = 0;
	}

	public boolean canPlay(int units) {
		if(units == 4) {
			return artillery + jokers >= 3;
		} else if(units == 6) {
			return infantry + jokers >= 3;
		} else if(units == 8) {
			return cavalry + jokers >= 3;
		} else if(units == 10) {
			return (artillery + cavalry + infantry >= 1 && jokers >= 2)
					|| (artillery >= 1 && cavalry >= 1 && infantry >= 1)
					|| (jokers >= 1 && (
							(artillery >= 1 && cavalry >= 1)
							|| (cavalry >= 1 && infantry >= 1)
							|| (artillery >= 1 && infantry >= 1)));
		}
		return false;
	}

	public Cards getCombination(int units) {
		if(units == 4) {
			return getBestCombination(Card.ARTILLERY, artillery);
		} else if(units == 6) {
			return getBestCombination(Card.INFANTRY, infantry);
		} else if(units == 8) {
			return getBestCombination(Card.CAVALRY, cavalry);
		} else if(artillery >= 1 && cavalry >= 1 && infantry >= 1) {
			return new Cards(1, 1, 1, 0);
		} else if(artillery >= 1 && cavalry >= 1) {
			return new Cards(1, 1, 0, 1);
		} else if(infantry >= 1 && cavalry >= 1) {
			return new Cards(0, 1, 1, 1);
		} else if(infantry >= 1 && artillery >= 1) {
			return new Cards(1, 0, 1, 1);
		} else if(artillery >= 1) {
			return new Cards(1, 0, 0, 2);
		} else if(cavalry >= 1) {
			return new Cards(0, 1, 0, 2);
		}
		return new Cards(0, 0, 1, 2);
	}

	private Cards getBestCombination(Card type, int amount) {
		Cards out = new Cards();
		if(amount >= 3) {
			out.add(type);
			out.add(type);
			out.add(type);
		} else if(amount == 2) {
			out.add(type);
			out.add(type);
			out.setJokers(1);
		} else {
			out.add(type);
			out.setJokers(2);
		}
		return out;
	}
}
