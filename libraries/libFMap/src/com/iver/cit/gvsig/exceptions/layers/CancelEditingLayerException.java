package com.iver.cit.gvsig.exceptions.layers;

import java.util.Hashtable;
import java.util.Map;

import org.gvsig.exceptions.BaseException;
/**
 * @author Vicente Caballero Navarro
 */
public class CancelEditingLayerException extends BaseException {

	private String layer = null;

	public CancelEditingLayerException(String layer,Throwable exception) {
		this.layer = layer;
		init();
		initCause(exception);
	}

	private void init() {
		messageKey = "error_cancel_editing_layer";
		formatString = "Cannot cancel editing of layer: %(layer). ";
	}

	protected Map values() {
		Hashtable params = new Hashtable();
		params.put("layer",layer);
		return params;
	}

}
