package org.ee.logger.system;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.ee.logger.AbstractLogger;

public class SystemLogger extends AbstractLogger {
	@Override
	public void log(Level level, Object message) {
		PrintStream out;
		if(level.intValue() >= Level.WARNING.intValue()) {
			out = System.err;
		} else {
			out = System.out;
		}
		out.print("[" + level + "] " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()) + "  ");
		out.println(message);
	}
}
