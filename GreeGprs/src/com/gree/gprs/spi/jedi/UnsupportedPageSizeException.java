package com.gree.gprs.spi.jedi;

public class UnsupportedPageSizeException extends DriverException {

	/**
	 * Constructs a new {@code UnsupportedPageSizeException} instance with
	 * {@code null} as its detailed reason message.
	 */
	public UnsupportedPageSizeException() {
		super();
	}

	/**
	 * Constructs a new {@code UnsupportedPageSizeException} instance with the
	 * specified detailed reason message. The error message string {@code message}
	 * can later be retrieved by the {@link Throwable#getMessage() getMessage}
	 * method.
	 *
	 * @param message
	 *            the detailed reason of the exception (may be {@code null}).
	 */
	public UnsupportedPageSizeException(String message) {
		super(message);
	}
}
