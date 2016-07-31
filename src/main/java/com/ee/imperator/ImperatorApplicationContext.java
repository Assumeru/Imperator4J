package com.ee.imperator;

import org.ee.config.Config;
import org.ee.web.ApplicationContext;

import com.ee.imperator.api.Api;
import com.ee.imperator.crypt.PasswordHasher;
import com.ee.imperator.data.State;
import com.ee.imperator.i18n.ClientSideLanguageProvider;
import com.ee.imperator.map.MapProvider;
import com.ee.imperator.template.TemplateProvider;
import com.ee.imperator.url.UrlBuilder;

public interface ImperatorApplicationContext extends ApplicationContext {
	Config getConfig();

	State getState();

	ClientSideLanguageProvider getLanguageProvider();

	MapProvider getMapProvider();

	UrlBuilder getUrlBuilder();

	Imperator getApplication();

	TemplateProvider getTemplateProvider();

	Api getApi();

	PasswordHasher getHasher();
}
