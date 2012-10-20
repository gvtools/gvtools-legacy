package com.hardcode.gdbms.driver.exceptions;

public class InitializeDriverException extends ReadDriverException {

	public InitializeDriverException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_initialize_driver";
		formatString = "Can´t initialize driver: %(driver) ";
	}

}
