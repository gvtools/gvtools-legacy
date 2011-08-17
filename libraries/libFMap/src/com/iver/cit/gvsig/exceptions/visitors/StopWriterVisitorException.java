package com.iver.cit.gvsig.exceptions.visitors;


/**
 * @author Vicente Caballero Navarro
 */
public class StopWriterVisitorException extends StopVisitorException {
	public StopWriterVisitorException(String layer,Throwable exception) {
		super(layer,exception);
		init();
		// initCause(exception);
	}

	private void init() {
		messageKey = "error_stop_writer_visitor";
		formatString = "Cannot stop writer visitor for layer: %(layer). ";
	}
}

// [eiel-gestion-excepciones]
