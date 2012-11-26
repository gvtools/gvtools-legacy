package org.gvsig.layer;

public class CompositeLayerFilter implements LayerFilter {
	private LayerFilter[] filters;

	public CompositeLayerFilter(LayerFilter... filters) {
		this.filters = filters;
	}

	@Override
	public boolean accepts(Layer layer) {
		assert false;
		return false;
	}
}
