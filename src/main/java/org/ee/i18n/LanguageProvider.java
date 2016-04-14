package org.ee.i18n;

import org.ee.i18n.Language.TextDirection;

public interface LanguageProvider {
	Language createLanguage(String lang, String locale, TextDirection direction);
}
