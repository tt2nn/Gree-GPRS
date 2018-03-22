package com.gree.gprs.spi.jedi;

public class UnalignedAddressException extends DriverException {

	/**
	 * Constructs a new {@code UnalignedAddressException} instance with {@code null}
	 * as its detailed reason message.
	 */
	public UnalignedAddressException() {
		super();
	}

	/**
	 * Constructs a new {@code UnalignedAddressException} instance with the
	 * specified detailed reason message. The error message string {@code message}
	 * can later be retrieved by the {@link Throwable#getMessage() getMessage}
	 * method.
	 *
	 * @param message
	 *            the detailed reason of the exception (may be {@code null}).
	 */
	public UnalignedAddressException(String message) {
		super(message);
	}
}
