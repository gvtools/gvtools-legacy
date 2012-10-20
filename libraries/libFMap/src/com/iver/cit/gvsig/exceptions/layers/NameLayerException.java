package com.iver.cit.gvsig.exceptions.layers;

/**
 * @author Vicente Caballero Navarro
 */
public class NameLayerException extends LoadLayerException {

	public NameLayerException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_name_layer";
		formatString = "Could not rename layer: %(layer). ";
	}

}
