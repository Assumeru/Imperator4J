package org.ee.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;

public abstract class AbstractLogger implements Logger {
	@Override
	public void v(Object message, Throwable throwable) {
		log(Level.FINER, message, throwable);
	}

	@Override
	public void v(Object message) {
		log(Level.FINER, message);
	}

	@Override
	public void v(Throwable throwable) {
		log(Level.FINER, throwable);
	}

	@Override
	public void d(Object message, Throwable throwable) {
		log(Level.FINE, message, throwable);
	}

	@Override
	public void d(Object message) {
		log(Level.FINE, message);
	}

	@Override
	public void d(Throwable throwable) {
		log(Level.FINE, throwable);
	}

	@Override
	public void i(Object message, Throwable throwable) {
		log(Level.INFO, message, throwable);
	}

	@Override
	public void i(Object message) {
		log(Level.INFO, message);
	}

	@Override
	public void i(Throwable throwable) {
		log(Level.INFO, throwable);
	}

	@Override
	public void w(Object message, Throwable throwable) {
		log(Level.WARNING, message, throwable);
	}

	@Override
	public void w(Object message) {
		log(Level.WARNING, message);
	}

	@Override
	public void w(Throwable throwable) {
		log(Level.WARNING, throwable);
	}

	@Override
	public void e(Object message, Throwable throwable) {
		log(Level.SEVERE, message, throwable);
	}

	@Override
	public void e(Object message) {
		log(Level.SEVERE, message);
	}

	@Override
	public void e(Throwable throwable) {
		log(Level.SEVERE, throwable);
	}

	@Override
	public void log(Level level, Object message, Throwable throwable) {
		if(throwable == null) {
			log(level, message);
		} else {
			log(level, String.valueOf(message) + "\n" + getThrowableString(throwable));
		}
	}

	@Override
	public void log(Level level, Throwable throwable) {
		log(level, throwable == null ? null : getThrowableString(throwable));
	}

	private String getThrowableString(Throwable throwable) {
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(message);
		throwable.printStackTrace(writer);
		writer.flush();
		return message.toString();
	}
}
