package org.ee.web;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;

import org.ee.config.Config;
import org.ee.web.request.AbstractRequestResolver;

public abstract class WebApplication extends Application {
	private static ServletContext context;
	private static Config config;

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(getRequestResolver());
		return classes;
	}

	protected abstract Class<? extends AbstractRequestResolver> getRequestResolver();

	public static ServletContext getContext() {
		return context;
	}

	protected static void setContext(ServletContext context) {
		WebApplication.context = context;
	}

	public static Config getConfig() {
		return config;
	}

	public static void setConfig(Config config) {
		WebApplication.config = config;
	}

	public static File[] getFiles(final String path, final String suffix) {
		return getFiles(path, (dir, name) -> name.endsWith(suffix));
	}

	public static File[] getFiles(String path, FilenameFilter filter) {
		return getFile(path).listFiles(filter);
	}

	public static File getFile(String path) {
		return new File(context.getRealPath(path));
	}
}
