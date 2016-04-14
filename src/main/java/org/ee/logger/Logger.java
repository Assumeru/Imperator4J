package org.ee.logger;

import java.util.logging.Level;

public interface Logger {
	void v(Object message, Throwable throwable);

	void v(Object message);

	void v(Throwable throwable);

	void d(Object message, Throwable throwable);

	void d(Object message);

	void d(Throwable throwable);

	void i(Object message, Throwable throwable);

	void i(Object message);

	void i(Throwable throwable);

	void w(Object message, Throwable throwable);

	void w(Object message);

	void w(Throwable throwable);

	void e(Object message, Throwable throwable);

	void e(Object message);

	void e(Throwable throwable);

	void log(Level level, Object message);

	void log(Level level, Object message, Throwable throwable);

	void log(Level level, Throwable throwable);
}
