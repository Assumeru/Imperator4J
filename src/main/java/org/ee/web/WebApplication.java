package org.ee.web;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.ee.web.request.AbstractRequestResolver;

public abstract class WebApplication extends Application {
	private static ServletContext context;

	public WebApplication(@Context ServletContext context) {
		WebApplication.context = context;
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(getRequestResolver());
		return classes;
	}

	protected abstract Class<? extends AbstractRequestResolver> getRequestResolver();

	protected static ServletContext getContext() {
		return context;
	}

	public static File[] getFiles(final String path, final String suffix) {
		return getFiles(path, new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(suffix);
			}
		});
	}

	public static File[] getFiles(String path, FilenameFilter filter) {
		return new File(context.getRealPath(path)).listFiles(filter);
	}
}
