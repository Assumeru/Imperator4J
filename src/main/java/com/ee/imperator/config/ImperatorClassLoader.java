package com.ee.imperator.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

import org.ee.config.ConfigurationException;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

public class ImperatorClassLoader extends URLClassLoader {
	private static final Logger LOG = LogManager.createLogger();
	private static final String PROPERTY = ImperatorClassLoader.class.getName() + ".dir";
	private static final Pattern PATTERN = Pattern.compile("(.*?)(\\.jar)", Pattern.CASE_INSENSITIVE);

	public ImperatorClassLoader() {
		super(getUrls(), Thread.currentThread().getContextClassLoader());
	}

	private static URL[] getUrls() {
		File directory = getDirectory();
		File[] jars = directory.listFiles((dir, name) -> PATTERN.matcher(name).matches());
		try {
			URL[] urls = new URL[jars.length];
			for(int i = 0; i < jars.length; i++) {
				LOG.d("Adding jar: " + jars[i]);
				urls[i] = jars[i].toURI().toURL();
			}
			return urls;
		} catch (MalformedURLException e) {
			throw new ConfigurationException("Failed to init class loader", e);
		}
	}

	private static File getDirectory() {
		String directory = System.getProperty(PROPERTY);
		if(directory == null) {
			LOG.w("Using default plugin directory, use jvm argument -D" + PROPERTY + "=<directory> to define another directory.");
			directory = "imperator/lib/";
		}
		File file = new File(directory);
		if(!file.exists()) {
			LOG.w("Plugin directory not found: " + file.getAbsolutePath());
		} else if(!file.isDirectory()) {
			throw new ConfigurationException(file + " is not a directory");
		}
		return file;
	}
}
