package org.ee.web.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

public class DefaultResponseWriter implements ResponseWriter {
	private static final Logger LOG = LogManager.createLogger();

	@Override
	public void write(Object response, OutputStream output) throws IOException {
		if(response != null) {
			if(response instanceof String) {
				writeString((String) response, output);
			} else if(response instanceof ByteArrayOutputStream) {
				((ByteArrayOutputStream) response).writeTo(output);
			} else if(response instanceof InputStream) {
				IOUtils.copy((InputStream) response, output);
			} else {
				LOG.w("Unknown output: " + response.getClass());
				writeString(response.toString(), output);
			}
		}
	}

	private void writeString(String response, OutputStream output) throws IOException {
		output.write(response.getBytes("UTF-8"));
	}
}
