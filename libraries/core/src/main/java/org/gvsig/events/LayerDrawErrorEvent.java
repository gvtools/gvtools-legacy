package org.gvsig.events;

import geomatico.events.Event;

import org.gvsig.layer.Layer;

public class LayerDrawErrorEvent implements Event<LayerDrawErrorHandler> {

	private Layer layer;
	private String message;
	private Throwable cause;

	public LayerDrawErrorEvent(Layer layer, String message, Throwable cause) {
		this.layer = layer;
		this.message = message;
		this.cause = cause;
	}

	@Override
	public void dispatch(LayerDrawErrorHandler handler) {
		handler.error(layer, message, cause);
	}

}
