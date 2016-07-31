package com.ee.imperator.config;

import java.io.File;
import java.io.IOException;

import org.ee.config.properties.PropertiesConfig;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

import com.ee.imperator.ImperatorApplicationContext;

public class ImperatorConfig extends PropertiesConfig {
	private static final Logger LOG = LogManager.createLogger();

	public ImperatorConfig(ImperatorApplicationContext context) throws IOException {
		super(getConfigPath(context));
	}

	private static File getConfigPath(ImperatorApplicationContext context) {
		String path = System.getProperty(ImperatorConfig.class.getName() + ".file");
		if(path != null) {
			File file = new File(path);
			if(file.exists()) {
				return file;
			}
			LOG.w("Failed to find " + file);
		}
		LOG.w("Falling back on /WEB-INF/default.properties");
		return context.getFile("/WEB-INF/default.properties");
	}
}
