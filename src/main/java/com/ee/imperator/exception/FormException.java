package com.ee.imperator.exception;

public class FormException extends Exception {
	private static final long serialVersionUID = 5410071100866561770L;

	public FormException() {
		super();
	}

	public FormException(String message, Throwable cause) {
		super(message, cause);
	}

	public FormException(String message) {
		super(message);
	}

	public FormException(Throwable cause) {
		super(cause);
	}
}
