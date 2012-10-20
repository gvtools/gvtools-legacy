package com.iver.cit.gvsig.exceptions.expansionfile;

import java.util.Hashtable;
import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;

/**
 * @author Vicente Caballero Navarro
 */
public class ExpansionFileException extends ReadDriverException {

	private String file = null;

	public ExpansionFileException(String file, Throwable exception) {
		super(file, exception);
		this.file = file;
		init();
		initCause(exception);
	}

	private void init() {
		messageKey = "error_expansion_file";
		formatString = "Error reading expansion file: %(file). ";
	}

	protected Map values() {
		Hashtable params = new Hashtable();
		params.put("file", file);
		return params;
	}
}
