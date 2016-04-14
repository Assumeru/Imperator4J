package org.ee.i18n;

public abstract class AbstractLanguage implements Language {
	private final String lang;
	private final String locale;
	private final TextDirection direction;

	protected AbstractLanguage(String lang, String locale, TextDirection direction) {
		this.lang = lang;
		this.locale = locale;
		this.direction = direction;
	}

	@Override
	public String getLang() {
		return lang;
	}

	@Override
	public String getLocale() {
		return locale;
	}

	@Override
	public TextDirection getDirection() {
		return direction;
	}

	@Override
	public String getHtmlLang() {
		String locale = getLocale();
		if(locale != null && !locale.isEmpty()) {
			return getLang() + "-" + locale;
		}
		return getLang();
	}

	@Override
	public CharSequence __(Object... vars) {
		return translate(vars);
	}
}
