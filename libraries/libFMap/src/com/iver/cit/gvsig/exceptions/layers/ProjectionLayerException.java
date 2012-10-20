package com.iver.cit.gvsig.exceptions.layers;

/**
 * @author Vicente Caballero Navarro
 */
public class ProjectionLayerException extends LoadLayerException {

	public ProjectionLayerException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_projection_layer";
		formatString = "Failed to project layer: %(layer). ";
	}

}
