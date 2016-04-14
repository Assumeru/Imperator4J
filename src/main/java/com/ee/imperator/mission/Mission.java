package com.ee.imperator.mission;

import org.ee.i18n.Language;

import com.ee.imperator.user.Player;

public interface Mission {
	public String getName();

	public int getId();

	public String getDescription(Language language);

	public boolean containsEliminate();

	public Integer getFallback();

	public void setFallback(int id);

	public boolean hasBeenCompleted(Player player);
}
