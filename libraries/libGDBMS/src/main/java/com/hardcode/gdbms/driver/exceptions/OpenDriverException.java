package com.hardcode.gdbms.driver.exceptions;

public class OpenDriverException extends InitializeDriverException {

	public OpenDriverException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_open_driver";
		formatString = "Can´t open driver of the driver: %(driver) ";
	}

}
