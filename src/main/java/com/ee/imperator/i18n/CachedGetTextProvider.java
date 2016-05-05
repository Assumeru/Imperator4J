package com.ee.imperator.i18n;

import org.ee.i18n.CachedLanguageProvider;
import org.ee.i18n.gettext.GetTextProvider;

import com.ee.imperator.Imperator;

public class CachedGetTextProvider extends CachedLanguageProvider {
	public CachedGetTextProvider() {
		super(new GetTextProvider(Imperator.getFile(Imperator.getConfig().getString(CachedGetTextProvider.class, "path"))));
	}
}
