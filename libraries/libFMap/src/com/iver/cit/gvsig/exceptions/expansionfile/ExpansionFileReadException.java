package com.iver.cit.gvsig.exceptions.expansionfile;

import com.iver.cit.gvsig.fmap.edition.ExpansionFile;

/**
 * Exception thrown when an error ocurred in an access to the ExpansionFile.
 * Typically an ExpansionFileReadExpection is thrown when the current layer is
 * being edited.
 * 
 * @see ExpansionFile
 * @author Vicente Caballero Navarro
 */
public class ExpansionFileReadException extends ExpansionFileException {

	public ExpansionFileReadException(String l, Throwable exception) {
		super(l, exception);
		init();
	}

	/**
	 *
	 */
	private void init() {
		messageKey = "error_read_expansion_file";
		formatString = "Error reading expansion file: %(file). ";
	}

}
