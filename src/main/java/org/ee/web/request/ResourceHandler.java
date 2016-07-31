package org.ee.web.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.request.filter.RequestFilter;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;

public abstract class ResourceHandler implements RequestFilter {
	private static final Logger LOG = LogManager.createLogger();

	@Override
	public Response handle(Request request) {
		final File file = getFile(request);
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try(InputStream input = new BufferedInputStream(new FileInputStream(file))) {
			IOUtils.copy(input, output);
		} catch (FileNotFoundException e) {
			LOG.w("Could not find resource", e);
			return new SimpleResponse(Status.NOT_FOUND);
		} catch (IOException e) {
			LOG.e("Error reading resource " + file, e);
			return new SimpleResponse(Status.INTERNAL_SERVER_ERROR);
		}
		Response response = new SimpleResponse(Status.OK, output);
		response.setContentType(getType(request));
		return response;
	}

	protected abstract String getType(Request request);

	protected File getFile(Request request) {
		return request.getContext().getFile(request.getPath());
	}
}
