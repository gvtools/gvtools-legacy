package com.iver.cit.gvsig.exceptions.layers;

/**
 * @author Vicente Caballero Navarro
 */
public class ReprojectLayerException extends LoadLayerException {

	public ReprojectLayerException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_projection_layer";
		formatString = "Failed to reproject layer: %(layer). ";
	}

}
