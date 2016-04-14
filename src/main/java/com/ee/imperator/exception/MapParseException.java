package com.ee.imperator.exception;

public class MapParseException extends Exception {
	private static final long serialVersionUID = -4960964142505332850L;

	public MapParseException() {
		super();
	}

	public MapParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public MapParseException(String message) {
		super(message);
	}

	public MapParseException(Throwable cause) {
		super(cause);
	}
}
