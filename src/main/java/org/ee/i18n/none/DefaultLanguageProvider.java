package org.ee.i18n.none;

import java.util.Locale;

import org.ee.i18n.Language;
import org.ee.i18n.Language.TextDirection;
import org.ee.i18n.LanguageProvider;

public class DefaultLanguageProvider implements LanguageProvider {
	@Override
	public Language getLanguage(Locale locale, TextDirection direction) {
		return new DefaultLanguage(locale, direction);
	}
}
