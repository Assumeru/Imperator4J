package com.ee.imperator;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;

@WebListener
public class ImperatorApplicationListener implements ServletContextListener {
	private static final Logger LOG = LogManager.createLogger();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(Imperator.getState() != null) {
			try {
				Imperator.getState().close();
			} catch (IOException e) {
				LOG.e("Failed to close dataProvider", e);
			}
		}
	}
}
