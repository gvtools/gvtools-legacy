package org.gvsig.layer.filter;

import org.gvsig.layer.Layer;

public class VectorialFilter implements LayerFilter {
	@Override
	public boolean accepts(Layer layer) {
		return layer.isVectorial();
	}
}
