package org.ee.i18n;

import java.util.Locale;

import org.ee.i18n.Language.TextDirection;

public interface LanguageProvider {
	Language getLanguage(Locale locale, TextDirection direction);

	default Language getLanguage(Locale locale) {
		return getLanguage(locale, TextDirection.LTR);
	}
}
