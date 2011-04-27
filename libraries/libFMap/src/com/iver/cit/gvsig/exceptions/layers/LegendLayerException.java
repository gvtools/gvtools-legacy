package com.iver.cit.gvsig.exceptions.layers;

/**
 * @author Vicente Caballero Navarro
 */
public class LegendLayerException extends LoadLayerException {

	public LegendLayerException(String l,Throwable exception) {
		super(l,exception);
		init();
	}
	/**
	 *
	 */
	private void init() {
		messageKey = "error_legend_layer";
		formatString = "Error loading legend for layer: %(layer). ";
	}

}
