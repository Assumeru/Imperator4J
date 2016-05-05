package org.ee.i18n;

import java.util.Locale;

import org.ee.cache.SoftReferenceCache;
import org.ee.i18n.Language.TextDirection;

public class CachedLanguageProvider implements LanguageProvider {
	private final SoftReferenceCache<String, Language> cache = new SoftReferenceCache<>(0);
	private final LanguageProvider provider;

	public CachedLanguageProvider(LanguageProvider provider) {
		this.provider = provider;
	}

	@Override
	public Language createLanguage(String lang, String locale, TextDirection direction) {
		String key = getKey(lang, locale);
		Language out = cache.get(key);
		if(out == null) {
			out = provider.createLanguage(lang, locale, direction);
			cache.put(key, out);
		}
		return out;
	}

	private String getKey(String lang, String locale) {
		return (lang + "_" + locale).toLowerCase(Locale.US);
	}
}
