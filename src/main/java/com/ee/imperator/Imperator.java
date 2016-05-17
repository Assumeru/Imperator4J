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
import com.ee.imperator.data.ChatProvider;
import com.ee.imperator.data.DataProvider;
import com.ee.imperator.data.GameProvider;
import com.ee.imperator.data.JoinedDataProvider;
import com.ee.imperator.data.MapProvider;
import com.ee.imperator.data.MemberProvider;
import com.ee.imperator.exception.ConfigurationException;
import com.ee.imperator.i18n.ClientSideLanguageProvider;
import com.ee.imperator.request.RequestResolver;
import com.ee.imperator.template.TemplateProvider;
import com.ee.imperator.url.UrlBuilder;

public class Imperator extends WebApplication {
	private static final Logger LOG = LogManager.createLogger();
	private static DataProvider dataProvider;
	private static PasswordHasher hasher;
	private static UrlBuilder urlBuilder;
	private static ClientSideLanguageProvider languageProvider;
	private static TemplateProvider templateProvider;

	public Imperator(@Context ServletContext context) {
		super(context);
		initConfig();
		initDataProvider();
		initHasher();
		initUrlBuilder();
		initLanguage();
		initTemplateProvider();
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
			throw new RuntimeException("Failed to init config", e);
		}
	}

	private void initDataProvider() {
		try {
			dataProvider = new JoinedDataProvider(getProviderInstance(GameProvider.class),
					getProviderInstance(MemberProvider.class),
					getProviderInstance(MapProvider.class),
					getProviderInstance(ChatProvider.class));
		} catch(Exception e) {
			throw new RuntimeException("Failed to init data provider", e);
		}
	}

	private <T> T getProviderInstance(Class<T> type) {
		try {
			return ReflectionUtils.getSubclass(Imperator.getConfig().getClass(type, null), type).newInstance();
		} catch(Exception e) {
			throw new ConfigurationException("Failed to create provider for " + type, e);
		}
	}

	private void initHasher() {
		try {
			hasher = ReflectionUtils.getSubclass(getConfig().getClass(PasswordHasher.class, null, BCryptHasher.class), PasswordHasher.class).newInstance();
		} catch(Exception e) {
			throw new ConfigurationException("Failed to init password hasher", e);
		}
	}

	private void initUrlBuilder() {
		try {
			urlBuilder = ReflectionUtils.getSubclass(getConfig().getClass(UrlBuilder.class, null), UrlBuilder.class).newInstance();
		} catch(Exception e) {
			throw new ConfigurationException("Failed to init url builder", e);
		}
	}

	private void initLanguage() {
		try {
			languageProvider = ReflectionUtils.getSubclass(getConfig().getClass(ClientSideLanguageProvider.class, null), ClientSideLanguageProvider.class).newInstance();
		} catch(Exception e) {
			LOG.e("Failed to init language provider, falling back on default implementation", e);
		}
	}

	private void initTemplateProvider() {
		try {
			templateProvider = ReflectionUtils.getSubclass(getConfig().getClass(TemplateProvider.class, null), TemplateProvider.class).newInstance();
		} catch(Exception e) {
			throw new ConfigurationException("Failed to init template provider", e);
		}
	}

	@Override
	protected Class<RequestResolver> getRequestResolver() {
		return RequestResolver.class;
	}

	public static DataProvider getData() {
		return dataProvider;
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
}
