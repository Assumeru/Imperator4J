package org.ee.i18n;

public interface Language {
	static enum TextDirection {
		LTR, RTL;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	String getLang();

	String getLocale();

	String getHtmlLang();

	TextDirection getDirection();

	/**
	 * Alias of {@link #translate(Object...)}
	 */
	public default CharSequence __(Object... vars) {
		return translate(vars);
	}

	CharSequence translate(Object... vars);

	Object resolve(Object... vars);
}
