package com.ee.imperator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ImperatorApplicationListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Imperator.init(sce.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Imperator.stop();
	}
}
