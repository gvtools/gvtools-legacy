package org.gvsig.events;

import geomatico.events.EventHandler;

import org.gvsig.layer.Layer;

public interface LayerDrawErrorHandler extends EventHandler {

	void error(Layer source, String message, Throwable problem);

}
