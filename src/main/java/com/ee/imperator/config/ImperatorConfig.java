package com.ee.imperator.config;

import java.io.File;
import java.io.IOException;

import org.ee.config.properties.PropertiesConfig;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

import com.ee.imperator.Imperator;

public class ImperatorConfig extends PropertiesConfig {
	private static final Logger LOG = LogManager.createLogger();

	public ImperatorConfig() throws IOException {
		super(getConfigPath());
	}

	private static File getConfigPath() {
		//TODO check other places
		LOG.w("Falling back on /WEB-INF/default.properties");
		return Imperator.getFile("/WEB-INF/default.properties");
	}
}
