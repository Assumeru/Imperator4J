package org.ee.web;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.ee.config.Config;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

@WebListener
public class WebApplicationListener implements ServletContextListener {
	private static final Logger LOG = LogManager.createLogger();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Config config = WebApplication.getConfig();
		if(config != null) {
			try {
				config.close();
			} catch (IOException e) {
				LOG.e("Failed to close config", e);
			}
		}
	}
}
