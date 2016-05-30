package com.ee.imperator.exception;

public class PageException extends RuntimeException {
	private static final long serialVersionUID = 4974845450684724106L;

	public PageException() {
		super();
	}

	public PageException(String message, Throwable cause) {
		super(message, cause);
	}

	public PageException(String message) {
		super(message);
	}

	public PageException(Throwable cause) {
		super(cause);
	}
}
