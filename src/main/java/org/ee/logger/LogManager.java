package org.ee.logger;

import org.ee.logger.system.SystemLogProvider;

public final class LogManager {
	private static LogProvider provider = new SystemLogProvider();

	private LogManager() {
	}

	public static Logger createLogger() {
		return provider.createLogger();
	}

	public static void setLogProvider(LogProvider provider) {
		LogManager.provider = provider;
	}
}
