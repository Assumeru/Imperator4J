package com.ee.imperator.exception;

import javax.ws.rs.core.Response.Status;

import org.ee.i18n.Language;

import com.ee.imperator.api.Api;

public class RequestException extends Exception {
	private static final long serialVersionUID = 6416489667172984755L;
	private String mode;
	private String type;

	public RequestException(String message, String mode, String type, Throwable cause) {
		super(message, cause);
		this.mode = mode;
		this.type = type;
	}

	public RequestException(String message, String mode, String type) {
		super(message);
		this.mode = mode;
		this.type = type;
	}

	public String getMode() {
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
