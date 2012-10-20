package com.iver.cit.gvsig.exceptions.layers;

/**
 * @author Vicente Caballero Navarro
 */
public class FileLayerException extends LoadLayerException {

	public FileLayerException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_file_layer";
		formatString = "File not found for layer: %(layer). ";
	}
}
