package com.ee.imperator.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.ee.collection.MapBuilder;
import org.ee.i18n.CachedLanguageProvider;
import org.ee.i18n.Language;
import org.ee.i18n.gettext.GetText;
import org.ee.i18n.gettext.GetTextProvider;
import org.ee.i18n.gettext.Mo;
import org.ee.i18n.gettext.MoParser;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.web.context.PageContext;

public class CachedGetTextProvider extends CachedLanguageProvider implements ClientSideLanguageProvider {
	private final Map<Mo, Map<String, Object>> cache;

	public CachedGetTextProvider(ImperatorApplicationContext context) {
		super(new GetTextProvider(context.getFile(context.getConfig().getString(CachedGetTextProvider.class, "path"))));
		cache = new WeakHashMap<>();
	}

	@Override
	public void addToPage(PageContext context) {
		PageContext.VARIABLE_JAVASCRIPT.add(context, "lib/jed.js");
		PageContext.VARIABLE_JAVASCRIPT.add(context, "gettext.js");
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "gettext", getMo(context.getUser().getLanguage()));
	}

	private Map<String, Object> getMo(Language language) {
		Mo mo = ((GetText) language).getMo();
		Map<String, Object> output = cache.get(mo);
		if(output == null) {
			output = createFrom(language, mo);
			cache.put(mo, output);
		}
		return output;
	}

	private synchronized Map<String, Object> createFrom(Language language, Mo mo) {
		Map<String, Object> output = cache.get(mo);
		if(output != null) {
			return output;
		}
		String pluralForms;
		Map<String, Object> translations;
		int plurals = 2;
		if(mo == null) {
			pluralForms = MoParser.DEFAULT_PLURAL_FORM_STRING;
			translations = new HashMap<>();
		} else {
			pluralForms = mo.getPluralForms();
			translations = new HashMap<>(mo.getTranslations());
			plurals = mo.getNumberOfPlurals();
		}
		translations.put("", new MapBuilder<>()
				.put("plural_forms", "nplurals=" + plurals + "; plural=" + pluralForms + ";")
				.put("lang", language.getLocale().getLanguage())
				.build());
		return new MapBuilder<String, Object>()
				.put("locale_data", new MapBuilder<>()
						.put("messages", translations)
						.build())
				.build();
	}
}
