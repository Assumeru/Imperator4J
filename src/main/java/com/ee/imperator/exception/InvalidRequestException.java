package com.ee.imperator.exception;

import org.ee.i18n.Language;

import com.ee.imperator.api.Api;

public class InvalidRequestException extends Exception {
	private static final long serialVersionUID = 6416489667172984755L;
	private String mode;
	private String type;

	public InvalidRequestException(String message, String mode, String type, Throwable cause) {
		super(message, cause);
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
}
