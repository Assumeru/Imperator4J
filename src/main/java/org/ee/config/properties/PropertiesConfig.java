package org.ee.config.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ee.config.AbstractConfig;

public class PropertiesConfig extends AbstractConfig {
	private Properties properties;

	public PropertiesConfig(Properties properties) {
		this.properties = properties;
	}

	public PropertiesConfig(File file) throws IOException {
		this.properties = new Properties();
		try(InputStream input = new FileInputStream(file)) {
			properties.load(input);
		}
	}

	@Override
	public String getString(String key) {
		return properties.getProperty(key);
	}
}
