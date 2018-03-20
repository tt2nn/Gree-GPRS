package com.gree.gprs.spi.jedi;

public class InvalidDeviceDescriptorException extends Exception {

	/**
	 * Constructs a new {@code InvalidDeviceDescriptorException} instance with
	 * {@code null} as its detailed reason message.
	 */
	public InvalidDeviceDescriptorException() {
		super();
	}

	/**
	 * Constructs a new {@code InvalidDeviceDescriptorException} instance with the
	 * specified detailed reason message. The error message string {@code message}
	 * can later be retrieved by the {@link Throwable#getMessage() getMessage}
	 * method.
	 *
	 * @param message
	 *            the detailed reason of the exception (may be {@code null}).
	 */
	public InvalidDeviceDescriptorException(String message) {
		super(message);
	}
}
