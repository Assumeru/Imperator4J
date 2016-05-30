package com.ee.imperator.user;

import java.util.Locale;

import org.ee.i18n.Language;

import com.ee.imperator.Imperator;

public class Member implements User, Comparable<Member> {
	private final int id;
	private final String name;
	private final Language language;
	private final boolean guest;
	private final boolean canDeleteMessages;
	private int score;
	private int wins;
	private int losses;

	public Member() {
		this(0);
	}

	public Member(int id) {
		this(id, "Guest", Imperator.getLanguageProvider().getLanguage(Locale.US), true, false, 0, 0, 0);
	}

	public Member(int id, String name, Language language, boolean guest, boolean canDeleteMessages, int score, int wins, int losses) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.guest = guest;
		this.canDeleteMessages = canDeleteMessages;
		this.score = score;
		this.wins = wins;
		this.losses = losses;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public Language getLanguage() {
		return language;
	}

	public boolean isGuest() {
		return guest;
	}

	public int getScore() {
		return score;
	}

	public int getLosses() {
		return losses;
	}

	public int getWins() {
		return wins;
	}

	@Override
	public int hashCode() {
		return 751 * id;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(obj instanceof User) {
			return ((User) obj).getId() == getId();
		}
		return false;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public boolean canDeleteMessages() {
		return canDeleteMessages;
	}

	@Override
	public int compareTo(Member o) {
		return Integer.compare(score, o.score);
	}
}
