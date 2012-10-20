package com.iver.cit.gvsig.exceptions.expansionfile;

/**
 * @author Vicente Caballero Navarro
 */
public class CloseExpansionFileException extends ExpansionFileException {

	public CloseExpansionFileException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_close_expansion_file";
		formatString = "Failed to close expansion file: %(file). ";
	}
}
