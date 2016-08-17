package com.ee.imperator;

import org.ee.config.Config;
import org.ee.config.ConfigurationException;
import org.ee.web.AbstractApplicationContext;

import com.ee.imperator.api.Api;
import com.ee.imperator.crypt.PasswordHasher;
import com.ee.imperator.crypt.csrf.CSRFTokenBuilder;
import com.ee.imperator.data.State;
import com.ee.imperator.i18n.ClientSideLanguageProvider;
import com.ee.imperator.map.MapProvider;
import com.ee.imperator.template.TemplateProvider;
import com.ee.imperator.url.UrlBuilder;

public class ImperatorContext extends AbstractApplicationContext implements ImperatorApplicationContext {
	private final Imperator imperator;

	protected ImperatorContext(Imperator imperator) {
		super(imperator.getServletContext());
		this.imperator = imperator;
	}

	@Override
	public Config getConfig() {
		return imperator.getConfig();
	}

	@Override
	public State getState() {
		return imperator.getState();
	}

	@Override
	public ClientSideLanguageProvider getLanguageProvider() {
		return imperator.getLanguageProvider();
	}

	@Override
	public MapProvider getMapProvider() {
		return imperator.getMapProvider();
	}

	@Override
	public UrlBuilder getUrlBuilder() {
		return imperator.getUrlBuilder();
	}

	@Override
	public Imperator getApplication() {
		return imperator;
	}

	@Override
	public TemplateProvider getTemplateProvider() {
		return imperator.getTemplateProvider();
	}

	@Override
	public PasswordHasher getHasher() {
		return imperator.getHasher();
	}

	@Override
	public Api getApi() {
		return imperator.getApi();
	}

	@Override
	public CSRFTokenBuilder getCsrfTokenBuilder() {
		return imperator.getCsrfTokenBuilder();
	}

	@Override
	public int getIntSetting(Class<?> type, String key) {
		return checkValue(getConfig().getInt(type, key), type, key);
	}

	@Override
	public long getLongSetting(Class<?> type, String key) {
		return checkValue(getConfig().getLong(type, key), type, key);
	}

	@Override
	public String getStringSetting(Class<?> type, String key) {
		String value = getConfig().getString(type, key);
		if(value == null || value.isEmpty()) {
			throw new ConfigurationException("Missing config value for " + Config.getKey(type, key));
		}
		return value;
	}

	@Override
	public String[] getStringsSetting(Class<?> type, String key) {
		return checkValue(getConfig().getStrings(type, key), type, key);
	}

	private <T> T checkValue(T value, Class<?> type, String key) {
		if(value == null) {
			throw new ConfigurationException("Missing config value for " + Config.getKey(type, key));
		}
		return value;
	}
}
