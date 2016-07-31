package org.ee.web.exception;

import org.ee.web.Status;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;

public class NotFoundException extends WebException {
	private static final long serialVersionUID = -5113405206019799313L;
	private static final Response NOT_FOUND = new SimpleResponse(Status.NOT_FOUND);

	public NotFoundException() {
		super(NOT_FOUND);
	}
}
