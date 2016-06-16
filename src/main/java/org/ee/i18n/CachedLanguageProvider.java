package org.ee.i18n;

import java.util.Locale;
import java.util.Map;

import org.ee.cache.SoftReferenceCache;
import org.ee.i18n.Language.TextDirection;

public class CachedLanguageProvider implements LanguageProvider {
	private final Map<Locale, Language> cache = new SoftReferenceCache<>(0);
	private final LanguageProvider provider;

	public CachedLanguageProvider(LanguageProvider provider) {
		this.provider = provider;
	}

	@Override
	public Language getLanguage(Locale locale, TextDirection direction) {
		Language out = cache.get(locale);
		if(out == null) {
			out = provider.getLanguage(locale, direction);
			cache.put(locale, out);
		}
		return out;
	}
}
