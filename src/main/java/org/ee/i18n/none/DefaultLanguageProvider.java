package org.ee.i18n.none;

import org.ee.i18n.Language;
import org.ee.i18n.LanguageProvider;
import org.ee.i18n.Language.TextDirection;

public class DefaultLanguageProvider implements LanguageProvider {
	@Override
	public Language createLanguage(String lang, String locale, TextDirection direction) {
		return new DefaultLanguage(lang, locale, direction);
	}
}
