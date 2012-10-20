package com.hardcode.gdbms.driver.exceptions;

public class ReloadDriverException extends ReadDriverException {
	public ReloadDriverException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_reload_driver";
		formatString = "Can´t reload driver: %(driver) ";
	}

}
