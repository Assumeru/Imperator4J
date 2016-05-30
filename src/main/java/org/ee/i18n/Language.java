package org.ee.i18n;

import java.util.Locale;

public interface Language {
	enum TextDirection {
		LTR, RTL;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	Locale getLocale();

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
