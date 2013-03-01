package com.sourceforgery.guicomponents.config;

public class ConfigParsingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConfigParsingException() {
		super();
	}

	public ConfigParsingException(final String message) {
		super(message);
	}

	public ConfigParsingException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConfigParsingException(final Throwable cause) {
		super(cause);
	}
}