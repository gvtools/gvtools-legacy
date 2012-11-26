package org.gvsig.layer.filter;

import org.gvsig.layer.Layer;

public class CompositeLayerFilter implements LayerFilter {
	private LayerFilter[] filters;

	public CompositeLayerFilter(LayerFilter... filters) {
		this.filters = filters;
	}

	@Override
	public boolean accepts(Layer layer) {
		for (LayerFilter filter : filters) {
			if (!filter.accepts(layer)) {
				return false;
			}
		}
		return true;
	}
}
