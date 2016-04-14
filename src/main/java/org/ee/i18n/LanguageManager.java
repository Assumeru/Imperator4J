package org.ee.i18n;

import org.ee.i18n.Language.TextDirection;
import org.ee.i18n.none.DefaultLanguageProvider;

public class LanguageManager {
	private static LanguageProvider provider = new DefaultLanguageProvider();

	public static Language createLanguage(String lang, String locale, TextDirection direction) {
		return provider.createLanguage(lang, locale, direction);
	}

	public static Language createLanguage(String lang, String locale) {
		return provider.createLanguage(lang, locale, TextDirection.LTR);
	}

	public static void setLanguageProvider(LanguageProvider provider) {
		LanguageManager.provider = provider;
	}
}
