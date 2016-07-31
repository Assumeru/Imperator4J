package org.ee.web.exception;

import org.ee.web.Status;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;

public class ForbiddenException extends WebException {
	private static final long serialVersionUID = -3903995142924892860L;
	private static final Response FORBIDDEN = new SimpleResponse(Status.FORBIDDEN);

	public ForbiddenException() {
		super(FORBIDDEN);
	}
}
