package com.iver.cit.gvsig.exceptions.expansionfile;

/**
 * @author Vicente Caballero Navarro
 */
public class ExpansionFileWriteException extends ExpansionFileException {

	public ExpansionFileWriteException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_write_expansion_file";
		formatString = "Error writing expansion file: %(file). ";
	}
}
