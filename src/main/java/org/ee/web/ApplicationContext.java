package org.ee.web;

import java.io.File;
import java.io.FilenameFilter;

public interface ApplicationContext {
	File[] getFiles(String path, String suffix);

	File[] getFiles(String path, FilenameFilter filter);

	File getFile(String path);

	String getContextPath();

	javax.servlet.ServletContext getContext();
}
