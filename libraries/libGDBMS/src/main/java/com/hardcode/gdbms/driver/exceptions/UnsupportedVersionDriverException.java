package com.hardcode.gdbms.driver.exceptions;


public class UnsupportedVersionDriverException extends OpenDriverException {
	private String description="";
	public UnsupportedVersionDriverException(String l,Throwable exception,String description) {
		super(l,exception);
		this.description=description;
		init();
	}
	public UnsupportedVersionDriverException(String l,Throwable exception) {
		super(l,exception);
		init();
	}
	/**
	 *
	 */
	private void init() {
		messageKey = "error_version_driver";
		formatString = "Can´t open file driver: %(driver) "+"\n"+description;
	}

}
