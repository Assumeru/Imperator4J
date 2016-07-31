package com.ee.imperator.i18n;

import org.ee.i18n.LanguageProvider;

import com.ee.imperator.web.context.PageContext;

public interface ClientSideLanguageProvider extends LanguageProvider {
	void addToPage(PageContext context);
}
