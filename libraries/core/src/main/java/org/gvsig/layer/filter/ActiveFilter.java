package org.gvsig.layer.filter;

import org.gvsig.layer.Layer;

public class ActiveFilter implements LayerFilter {
	@Override
	public boolean accepts(Layer layer) {
		return layer.isActive();
	}
}
