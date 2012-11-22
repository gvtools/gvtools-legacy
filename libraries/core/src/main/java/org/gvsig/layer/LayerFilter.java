package org.gvsig.layer;

public interface LayerFilter {

	LayerFilter VECTORIAL = new VectorialFilter();

	LayerFilter VECTORIAL_EDITING = new EditingFilter();

}
