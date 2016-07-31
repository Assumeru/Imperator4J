package org.ee.web.response;

import java.io.IOException;
import java.io.OutputStream;

public interface ResponseWriter {
	void write(Object response, OutputStream output) throws IOException;
}
