package com.iver.cit.gvsig.exceptions.layers;

/**
 * @author Vicente Caballero Navarro
 */
public class ReloadLayerException extends LoadLayerException {

	public ReloadLayerException(String l,Throwable exception) {
		super(l,exception);
		init();
	}
	
	public ReloadLayerException(String l) {
		super(l);
		init();
	}	
	
	/**
	 *
	 */
	private void init() {
		messageKey = "error_reload_layer";
		formatString = "Cannot reload layer: %(layer). ";
	}

}
