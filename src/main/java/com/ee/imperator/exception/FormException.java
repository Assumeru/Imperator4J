package com.ee.imperator.exception;

public class FormException extends Exception {
	private static final long serialVersionUID = 5410071100866561770L;
	private final String name;

	public FormException(String message, Throwable cause) {
		super(message, cause);
		name = null;
	}

	public FormException(String message) {
		this(message, (String) null);
	}

	public FormException(String message, String name) {
		super(message);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
