package com.ee.imperator.exception;

public class ConfigurationException extends RuntimeException {
	private static final long serialVersionUID = -8424452424742775477L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}
}
