package com.hardcode.gdbms.driver.exceptions;

import java.util.Hashtable;
import java.util.Map;

import org.gvsig.exceptions.BaseException;

public class WriteDriverException extends BaseException {

	private String driver = null;

	public WriteDriverException(String driver,Throwable exception) {
		this.driver = driver;
		init();
		if (exception != null){
			initCause(exception);
		}
	}

	private void init() {
		messageKey = "error_write_driver";
		formatString = "Can´t write the driver: %(driver) ";
	}

	protected Map values() {
		Hashtable params = new Hashtable();
		params.put("driver",driver);
		return params;
	}

}
