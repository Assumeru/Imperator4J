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
import com.ee.imperator.data.DataProvider;
import com.ee.imperator.data.GameProvider;
import com.ee.imperator.data.JoinedDataProvider;
import com.ee.imperator.data.MapProvider;
import com.ee.imperator.data.MemberProvider;
import com.ee.imperator.request.RequestResolver;

public class Imperator extends WebApplication {
	private static final Logger LOG = LogManager.createLogger();
	private static DataProvider dataProvider;
	private static PasswordHasher hasher;

	public Imperator(@Context ServletContext context) {
		super(context);
		initConfig();
		initDataProvider();
		initHasher();
	}

	private void initConfig() {
		try {
			String config = System.getProperty("com.ee.imperator.Config");
			if(config == null) {
				config = "com.ee.imperator.config.ImperatorConfig";
				LOG.w("Using default config, use jvm argument -Dcom.ee.imperator.Config=<class name> to define another config implementation.");
			}
			setConfig(ReflectionUtils.getSubclass(config, Config.class).newInstance());
		} catch (Exception e) {
			throw new RuntimeException("Failed to init config", e);
		}
	}

	private void initDataProvider() {
		try {
			dataProvider = new JoinedDataProvider(getProviderInstance(GameProvider.class), getProviderInstance(MemberProvider.class), getProviderInstance(MapProvider.class));
		} catch (Exception e) {
			throw new RuntimeException("Failed to init data provider", e);
		}
	}

	private <T> T getProviderInstance(Class<T> type) {
		try {
			return ReflectionUtils.getSubclass(Imperator.getConfig().getClass(type, null), type).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create provider for " + type, e);
		}
	}

	private void initHasher() {
		try {
			hasher = ReflectionUtils.getSubclass(getConfig().getClass(PasswordHasher.class, null, BCryptHasher.class), PasswordHasher.class).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to init password hasher", e);
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

	public static String buildLink(String url) {
		return getContext().getContextPath() + "/" + url;
	}
}
