package com.sourceforgery.nongui;

public class IORuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IORuntimeException() {
		super();
	}

	public IORuntimeException(final String message) {
		super(message);
	}

	public IORuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public IORuntimeException(final Throwable cause) {
		super(cause);
	}
}
