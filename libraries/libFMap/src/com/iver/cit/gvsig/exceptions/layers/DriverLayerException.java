package com.iver.cit.gvsig.exceptions.layers;

/**
 * @author Vicente Caballero Navarro
 */
public class DriverLayerException extends LoadLayerException {

	public DriverLayerException(String l,Throwable exception) {
		super(l,exception);
		init();
	}
	/**
	 *
	 */
	private void init() {
		messageKey = "error_driver_layer";
		formatString = "Error loading driver for layer datasource: %(layer). ";
	}
}
