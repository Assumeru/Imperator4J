package org.ee.i18n.gettext;

import org.ee.cache.SoftReferenceCache;
import org.ee.i18n.Language.TextDirection;
import org.ee.i18n.LanguageProvider;

public class GetTextProvider implements LanguageProvider {
	private SoftReferenceCache<String, GetText> cache = new SoftReferenceCache<>(0);

	@Override
	public GetText createLanguage(String lang, String locale, TextDirection direction) {
		String key = getKey(lang, locale);
		GetText out = cache.get(key);
		if(out == null) {
			out = new GetText(lang, locale, direction);
			cache.put(key, out);
		}
		return out;
	}

	private String getKey(String lang, String locale) {
		return lang + "_" + locale;
	}
}
