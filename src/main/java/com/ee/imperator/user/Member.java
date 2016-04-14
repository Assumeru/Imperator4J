package com.ee.imperator.user;

import org.ee.i18n.Language;
import org.ee.i18n.LanguageManager;

public class Member implements User {
	private final int id;
	private final String name;
	private final Language language;
	private final boolean loggedIn;
	private final int score;
	private final int wins;
	private final int losses;

	public Member() {
		this(0, "Guest", LanguageManager.createLanguage("en", "us"), false, 0, 0, 0);
	}

	public Member(int id, String name, Language language, boolean loggedIn, int score, int wins, int losses) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.loggedIn = loggedIn;
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

	@Override
	public String getProfileLink() {
		return null;
	}

	public Language getLanguage() {
		return language;
	}

	public boolean isLoggedIn() {
		return loggedIn;
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
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(obj instanceof User) {
			return ((User) obj).getId() == getId();
		}
		return false;
	}
}
