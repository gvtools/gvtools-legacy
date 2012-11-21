package org.gvsig.events;

import org.gvsig.layer.Layer;
import org.gvsig.map.MapContext;

import geomatico.events.EventHandler;

public interface LayerAddedHandler extends EventHandler {

	void layerAdded(MapContext map, Layer layer);
	
}
