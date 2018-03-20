package com.gree.gprs.spi.jedi;

public class DriverException extends Exception {

	/**
	 * Constructs a new {@code DriverException} instance with {@code null} as its
	 * detailed reason message.
	 */
	public DriverException() {
		super();
	}

	/**
	 * Constructs a new {@code DriverException} instance with the specified detailed
	 * reason message. The error message string {@code message} can later be
	 * retrieved by the {@link Throwable#getMessage() getMessage} method.
	 *
	 * @param message
	 *            the detailed reason of the exception (may be {@code null}).
	 */
	public DriverException(String message) {
		super(message);
	}
}
