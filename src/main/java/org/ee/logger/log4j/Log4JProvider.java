package org.ee.logger.log4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.ReflectionUtil;
import org.ee.logger.LogProvider;
import org.ee.logger.Logger;

public class Log4JProvider implements LogProvider {
	@Override
	public Logger createLogger() {
		return new Log4JLogger(LogManager.getLogger(ReflectionUtil.getCallerClass(3)));
	}
}
