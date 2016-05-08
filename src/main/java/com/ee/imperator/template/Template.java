package com.ee.imperator.template;

import java.io.IOException;
import java.io.OutputStream;

public interface Template {
	Template setVariable(String key, Object value);

	String process();

	void process(OutputStream output) throws IOException;
}
