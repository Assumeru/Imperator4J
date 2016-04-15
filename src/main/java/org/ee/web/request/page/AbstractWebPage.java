package org.ee.web.request.page;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.ee.web.request.Request;

public abstract class AbstractWebPage implements WebPage {
	private final String path;
	private final int status;

	public AbstractWebPage(String path, Status status) {
		this(path, status.getStatusCode());
	}

	public AbstractWebPage(String path, int status) {
		this.path = path;
		this.status = status;
	}

	@Override
	public boolean matches(String path) {
		return path.startsWith(getPath());
	}

	@Override
	public Response getResponse(Request request) {
		final ByteArrayOutputStream response = getResponseOutput(request);
		return Response.status(status).entity(new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				response.writeTo(output);
				output.flush();
			}
		}).build();
	}

	protected abstract ByteArrayOutputStream getResponseOutput(Request request);

	@Override
	public String getPath() {
		return path;
	}

	protected int getStatus() {
		return status;
	}

	@Override
	public String getName() {
		NavigationPage page = getClass().getAnnotation(NavigationPage.class);
		String name = null;
		if(page != null) {
			name = page.name();
		}
		return name == null ? "" : name;
	}
}
