package com.ee.imperator.exception;

import org.ee.i18n.Language;
import org.ee.web.Status;

import com.ee.imperator.api.Api;
import com.ee.imperator.api.handlers.Endpoint;

public class RequestException extends Exception {
	private static final long serialVersionUID = 6416489667172984755L;
	private final Endpoint.Mode mode;
	private final String type;

	public RequestException(String message, Endpoint.Mode mode, String type, Throwable cause) {
		super(message, cause);
		this.mode = mode;
		this.type = type;
	}

	public RequestException(String message, Endpoint.Mode mode, String type) {
		super(message);
		this.mode = mode;
		this.type = type;
	}

	public Endpoint.Mode getMode() {
		return mode;
	}

	public String getType() {
		return type;
	}

	public String getMessage(Language language) {
		return Api.getErrorMessage(language.translate(getMessage()).toString(), mode, type);
	}

	public Status getStatus() {
		return Status.INTERNAL_SERVER_ERROR;
	}
}
