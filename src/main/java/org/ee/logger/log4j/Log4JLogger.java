package org.ee.logger.log4j;

import java.util.logging.Level;

import org.apache.logging.log4j.Logger;
import org.ee.logger.AbstractLogger;

public class Log4JLogger extends AbstractLogger {
	private final Logger logger;

	Log4JLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void log(Level level, Object message) {
		logger.log(getLevel(level), message);
	}

	private org.apache.logging.log4j.Level getLevel(Level level) {
		int value = level.intValue();
		if(value <= Level.ALL.intValue()) {
			return org.apache.logging.log4j.Level.ALL;
		} else if(value <= Level.FINE.intValue()) {
			return org.apache.logging.log4j.Level.TRACE;
		} else if(value <= Level.CONFIG.intValue()) {
			return org.apache.logging.log4j.Level.DEBUG;
		} else if(value <= Level.INFO.intValue()) {
			return org.apache.logging.log4j.Level.INFO;
		} else if(value <= Level.WARNING.intValue()) {
			return org.apache.logging.log4j.Level.WARN;
		} else if(value <= Level.SEVERE.intValue()) {
			return org.apache.logging.log4j.Level.ERROR;
		} else if(value == Level.OFF.intValue()) {
			return org.apache.logging.log4j.Level.OFF;
		}
		return org.apache.logging.log4j.Level.FATAL;
	}
}
