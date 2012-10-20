package com.hardcode.gdbms.driver.exceptions;

import java.util.Hashtable;
import java.util.Map;

import org.gvsig.exceptions.BaseException;

public class InitializeWriterException extends BaseException {

	private String driver = null;

	public InitializeWriterException(String driver, Throwable exception) {
		this.driver = driver;
		init();
		initCause(exception);
	}

	private void init() {
		messageKey = "error_initialize_writer";
		formatString = "Can´t initialize writer: %(driver) ";
	}

	protected Map values() {
		Hashtable params = new Hashtable();
		params.put("driver", driver);
		return params;
	}

}
