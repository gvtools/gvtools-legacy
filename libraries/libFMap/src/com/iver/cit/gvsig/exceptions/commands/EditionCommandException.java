package com.iver.cit.gvsig.exceptions.commands;

import java.util.Hashtable;
import java.util.Map;

import org.gvsig.exceptions.BaseException;

/**
 * @author Vicente Caballero Navarro
 */
public class EditionCommandException extends BaseException {

	private String layer = null;

	public EditionCommandException(String layer, Throwable exception) {
		this.layer = layer;
		init();
		initCause(exception);
	}

	private void init() {
		messageKey = "error_command_editing";
		formatString = "Editing command failed for layer: %(layer). ";
	}

	protected Map values() {
		Hashtable params = new Hashtable();
		params.put("layer", layer);
		return params;
	}

}
