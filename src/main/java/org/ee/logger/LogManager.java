package org.ee.logger;

import org.ee.logger.system.SystemLogProvider;

public class LogManager {
	private static LogProvider provider = new SystemLogProvider();

	public static Logger createLogger() {
		return provider.createLogger();
	}

	public static void setLogProvider(LogProvider provider) {
		LogManager.provider = provider;
	}
}
