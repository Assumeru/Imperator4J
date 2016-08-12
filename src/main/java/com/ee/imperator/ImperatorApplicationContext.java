package com.ee.imperator;

import org.ee.config.Config;
import org.ee.web.ApplicationContext;

import com.ee.imperator.api.Api;
import com.ee.imperator.crypt.PasswordHasher;
import com.ee.imperator.crypt.csrf.CSRFTokenBuilder;
import com.ee.imperator.data.State;
import com.ee.imperator.exception.ConfigurationException;
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

	CSRFTokenBuilder getCsrfTokenBuilder();

	/**
	 * @see Config#getInt(Class, String)
	 * @param type The class to get a value for
	 * @param key The name of the value to get
	 * @return The configured value
	 * @throws ConfigurationException If no value could be found
	 */
	int getIntSetting(Class<?> type, String key);

	/**
	 * @see Config#getLong(Class, String)
	 * @param type The class to get a value for
	 * @param key The name of the value to get
	 * @return The configured value
	 * @throws ConfigurationException If no value could be found
	 */
	long getLongSetting(Class<?> type, String key);

	/**
	 * @see Config#getString(Class, String)
	 * @param type The class to get a value for
	 * @param key The name of the value to get
	 * @return The configured value
	 * @throws ConfigurationException If no value could be found or the value is an empty string
	 */
	String getStringSetting(Class<?> type, String key);

	/**
	 * @see Config#getStrings(Class, String)
	 * @param type The class to get a value for
	 * @param key The name of the value to get
	 * @return The configured value
	 * @throws ConfigurationException If no value could be found
	 */
	String[] getStringsSetting(Class<?> type, String key);
}
