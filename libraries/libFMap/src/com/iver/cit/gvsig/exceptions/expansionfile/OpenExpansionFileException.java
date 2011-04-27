package com.iver.cit.gvsig.exceptions.expansionfile;

/**
 * @author Vicente Caballero Navarro
 */
public class OpenExpansionFileException extends ExpansionFileException {

	public OpenExpansionFileException(String l,Throwable exception) {
		super(l,exception);
		init();
	}
	/**
	 *
	 */
	private void init() {
		messageKey = "error_open_expansion_file";
		formatString = "Failed to open expansion file: %(file). ";
	}

}
