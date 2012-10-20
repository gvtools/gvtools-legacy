package com.iver.cit.gvsig.exceptions.layers;

import java.util.Hashtable;
import java.util.Map;

import org.gvsig.exceptions.BaseException;

/**
 * @author Vicente Caballero Navarro
 */
public class LoadLayerException extends BaseException {
	private String layer = null;

	public LoadLayerException(String layer, Throwable exception) {
		this.layer = layer;
		init();
		initCause(exception);
	}

	public LoadLayerException(String layer) {
		this.layer = layer;
		init();
	}

	private void init() {
		messageKey = "error_load_layer";
		formatString = "Failed to load layer: %(layer). ";
	}

	protected Map values() {
		Hashtable params = new Hashtable();
		if (layer != null)
			params.put("layer", layer);
		return params;
	}

}
