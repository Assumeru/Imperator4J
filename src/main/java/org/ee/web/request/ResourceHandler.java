package org.ee.web.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

public abstract class ResourceHandler implements RequestHandler {
	private static final Logger LOG = LogManager.createLogger();

	@Override
	public Response getResponse(Request request) {
		final File file = getFile(request);
		final ByteArrayOutputStream response = new ByteArrayOutputStream();
		try(InputStream input = new BufferedInputStream(new FileInputStream(file))) {
			IOUtils.copy(input, response);
		} catch (FileNotFoundException e) {
			LOG.w("Could not find resource for " + file);
			return Response.status(Status.NOT_FOUND).build();
		} catch (IOException e) {
			LOG.e("Error reading resource " + file, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok().entity(new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				response.writeTo(output);
				output.flush();
			}
		}).type(getType(request)).build();
	}

	protected abstract MediaType getType(Request request);

	protected File getFile(Request request) {
		return new File(request.getServletContext().getRealPath(request.getPath()));
	}
}
