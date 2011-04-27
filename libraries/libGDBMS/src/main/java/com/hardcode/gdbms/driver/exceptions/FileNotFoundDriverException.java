package com.hardcode.gdbms.driver.exceptions;


public class FileNotFoundDriverException extends OpenDriverException {
	private String file="";
	public FileNotFoundDriverException(String l,Throwable exception,String file) {
		super(l,exception);
		this.file=file;
		init();
	}
	public FileNotFoundDriverException(String l,Throwable exception) {
		super(l,exception);
		init();
	}
	/**
	 *
	 */
	private void init() {
		messageKey = "error_file_not_find_driver";
		formatString = "Can´t find file: "+file+"\n in a driver: %(driver) ";
	}

}
