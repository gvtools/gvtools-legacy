package org.gvsig.layer.filter;

import org.gvsig.layer.Layer;

public interface LayerFilter {

	LayerFilter VECTORIAL = new VectorialFilter();

	LayerFilter VECTORIAL_EDITING = new EditingFilter();

	LayerFilter ACTIVE = new ActiveFilter();

	boolean accepts(Layer layer);
}
