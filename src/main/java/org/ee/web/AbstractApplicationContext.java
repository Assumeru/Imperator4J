package org.ee.web;

import java.io.File;
import java.io.FilenameFilter;

public class AbstractApplicationContext implements ApplicationContext {
	private final javax.servlet.ServletContext context;

	public AbstractApplicationContext(javax.servlet.ServletContext context) {
		this.context = context;
	}

	@Override
	public File[] getFiles(final String path, final String suffix) {
		return getFiles(path, (dir, name) -> name.endsWith(suffix));
	}

	@Override
	public File[] getFiles(String path, FilenameFilter filter) {
		return getFile(path).listFiles(filter);
	}

	@Override
	public File getFile(String path) {
		return new File(context.getRealPath(path));
	}

	@Override
	public String getContextPath() {
		return context.getContextPath();
	}

	@Override
	public javax.servlet.ServletContext getContext() {
		return context;
	}
}
