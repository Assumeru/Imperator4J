package org.ee.i18n;

import java.util.Locale;

public abstract class AbstractLanguage implements Language {
	private final Locale locale;
	private final TextDirection direction;

	protected AbstractLanguage(Locale locale, TextDirection direction) {
		this.locale = locale;
		this.direction = direction;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public TextDirection getDirection() {
		return direction;
	}

	@Override
	public CharSequence __(Object... vars) {
		return translate(vars);
	}
}
