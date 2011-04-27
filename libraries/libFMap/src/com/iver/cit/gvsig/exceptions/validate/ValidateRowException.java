package com.iver.cit.gvsig.exceptions.validate;

import java.util.Hashtable;
import java.util.Map;

import org.gvsig.exceptions.BaseException;
/**
 * @author Vicente Caballero Navarro
 */
public class ValidateRowException extends BaseException {
	private String layer = null;

	public ValidateRowException(String layer,Throwable exception) {
		this.layer = layer;
		init();
		initCause(exception);
	}

	private void init() {
		messageKey = "error_validate_row";
		formatString = "Cannot validate row for layer: %(layer). ";
	}

	protected Map values() {
		Hashtable params = new Hashtable();
		params.put("layer",layer);
		return params;
	}

}
