package org.gvsig.layer;

public interface LayerFilter {

	LayerFilter VECTORIAL = new VectorialFilter();

	LayerFilter VECTORIAL_EDITING = new EditingFilter();

	LayerFilter ACTIVE = new ActiveFilter();

	boolean accepts(Layer layer);
}
