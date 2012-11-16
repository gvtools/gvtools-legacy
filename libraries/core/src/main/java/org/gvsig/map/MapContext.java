package org.gvsig.map;

import org.gvsig.layer.Layer;

public interface MapContext {

	/**
	 * Get the root of the layer tree
	 * @return
	 */
	Layer getRootLayer();

}
