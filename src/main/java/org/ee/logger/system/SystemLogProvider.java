package org.ee.logger.system;

import org.ee.logger.LogProvider;
import org.ee.logger.Logger;

public class SystemLogProvider implements LogProvider {
	@Override
	public Logger createLogger() {
		return new SystemLogger();
	}
}
