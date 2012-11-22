package org.gvsig.layer;

public class CompositeLayerFilter implements LayerFilter {

	private LayerFilter[] filters;

	public CompositeLayerFilter(LayerFilter... filters) {
		this.filters = filters;
	}
}
