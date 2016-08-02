package com.ee.imperator.exception;

import org.ee.web.Status;

import com.ee.imperator.api.handlers.Endpoint;

public class InvalidRequestException extends RequestException {
	private static final long serialVersionUID = 4361443940202449253L;

	public InvalidRequestException(String message, Endpoint.Mode mode, String type, Throwable cause) {
		super(message, mode, type, cause);
	}

	public InvalidRequestException(String message, Endpoint.Mode mode, String type) {
		super(message, mode, type);
	}

	@Override
	public Status getStatus() {
		return Status.BAD_REQUEST;
	}
}
