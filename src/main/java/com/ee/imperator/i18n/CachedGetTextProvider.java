package com.ee.imperator.i18n;

import org.ee.i18n.CachedLanguageProvider;
import org.ee.i18n.gettext.GetTextProvider;

import com.ee.imperator.Imperator;

public class CachedGetTextProvider extends CachedLanguageProvider implements ClientSideLanguageProvider {
	public CachedGetTextProvider() {
		super(new GetTextProvider(Imperator.getFile(Imperator.getConfig().getString(CachedGetTextProvider.class, "path"))));
	}

	@Override
	public String getJavascript() {
		return "gettext.js";
	}
}
