package com.hardcode.gdbms.driver.exceptions;


public class CloseDriverException extends ReadDriverException {

	public CloseDriverException(String l,Throwable exception) {
		super(l,exception);
		init();
	}
	/**
	 *
	 */
	private void init() {
		messageKey = "error_close_driver";
		formatString = "Can´t close driver: %(driver) ";
	}
}
