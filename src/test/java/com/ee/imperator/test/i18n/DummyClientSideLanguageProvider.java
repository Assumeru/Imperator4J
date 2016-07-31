package com.ee.imperator.test.i18n;

import org.ee.i18n.none.DefaultLanguageProvider;

import com.ee.imperator.i18n.ClientSideLanguageProvider;
import com.ee.imperator.web.context.PageContext;

public class DummyClientSideLanguageProvider extends DefaultLanguageProvider implements ClientSideLanguageProvider {
	@Override
	public void addToPage(PageContext context) {
	}
}
