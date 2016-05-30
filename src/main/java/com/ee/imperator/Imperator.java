package com.ee.imperator;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.ee.config.Config;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.reflection.ReflectionUtils;
import org.ee.web.WebApplication;

import com.ee.imperator.crypt.PasswordHasher;
import com.ee.imperator.crypt.bcrypt.BCryptHasher;
import com.ee.imperator.data.ChatState;
import com.ee.imperator.data.GameState;
import com.ee.imperator.data.JoinedState;
import com.ee.imperator.data.MemberState;
import com.ee.imperator.data.State;
import com.ee.imperator.exception.ConfigurationException;
import com.ee.imperator.i18n.ClientSideLanguageProvider;
import com.ee.imperator.map.MapProvider;
import com.ee.imperator.request.RequestResolver;
import com.ee.imperator.template.TemplateProvider;
import com.ee.imperator.url.UrlBuilder;

public class Imperator extends WebApplication {
	private static final Logger LOG = LogManager.createLogger();
	private static State state;
	private static PasswordHasher hasher;
	private static UrlBuilder urlBuilder;
	private static ClientSideLanguageProvider languageProvider;
	private static TemplateProvider templateProvider;
	private static MapProvider mapProvider;

	public Imperator(@Context ServletContext context) {
		super(context);
		initConfig();
		initState();
		initHasher();
		urlBuilder = getProviderInstance(UrlBuilder.class);
		languageProvider = getProviderInstance(ClientSideLanguageProvider.class);
		templateProvider = getProviderInstance(TemplateProvider.class);
		mapProvider = getProviderInstance(MapProvider.class);
	}

	private void initConfig() {
		try {
			String config = System.getProperty("com.ee.imperator.Config");
			if(config == null) {
				config = "com.ee.imperator.config.ImperatorConfig";
				LOG.w("Using default config, use jvm argument -Dcom.ee.imperator.Config=<class name> to define another config implementation.");
			}
			setConfig(ReflectionUtils.getSubclass(config, Config.class).newInstance());
		} catch(Exception e) {
			throw new ConfigurationException("Failed to init config", e);
		}
	}

	private static void initState() {
		try {
			state = new JoinedState(getProviderInstance(GameState.class),
					getProviderInstance(MemberState.class),
					getProviderInstance(ChatState.class));
		} catch(Exception e) {
			throw new ConfigurationException("Failed to init state", e);
		}
	}

	private static <T> T getProviderInstance(Class<T> type) {
		try {
			return ReflectionUtils.getSubclass(Imperator.getConfig().getClass(type, null), type).newInstance();
		} catch(Exception e) {
			throw new ConfigurationException("Failed to create provider for " + type, e);
		}
	}

	private static void initHasher() {
		try {
			hasher = ReflectionUtils.getSubclass(getConfig().getClass(PasswordHasher.class, null, BCryptHasher.class), PasswordHasher.class).newInstance();
		} catch(Exception e) {
			throw new ConfigurationException("Failed to init password hasher", e);
		}
	}

	@Override
	protected Class<RequestResolver> getRequestResolver() {
		return RequestResolver.class;
	}

	public static State getState() {
		return state;
	}

	public static PasswordHasher getHasher() {
		return hasher;
	}

	public static ClientSideLanguageProvider getLanguageProvider() {
		return languageProvider;
	}

	public static UrlBuilder getUrlBuilder() {
		return urlBuilder;
	}

	public static TemplateProvider getTemplateProvider() {
		return templateProvider;
	}

	public static MapProvider getMapProvider() {
		return mapProvider;
	}
}
