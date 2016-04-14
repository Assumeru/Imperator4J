package com.ee.imperator.mission;

import org.ee.i18n.Language;

public abstract class AbstractMission implements Mission {
	private final int id;
	private final String name;
	private final String description;
	private Integer fallback;

	public AbstractMission(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getDescription(Language language, PlayerMission mission) {
		return language.translate(description).toString();
	}

	@Override
	public String getDescription(Language language) {
		return getDescription(language, null);
	}

	protected String getDescription() {
		return description;
	}

	@Override
	public Integer getFallback() {
		return fallback;
	}

	@Override
	public void setFallback(int id) {
		if(fallback != null) {
			throw new IllegalStateException("fallback has already been set");
		}
		fallback = id;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(obj instanceof Mission) {
			return getId() == ((Mission) obj).getId();
		}
		return false;
	}
}
