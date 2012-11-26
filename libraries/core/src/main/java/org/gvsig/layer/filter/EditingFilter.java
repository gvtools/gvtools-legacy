package org.gvsig.layer.filter;

import org.gvsig.layer.Layer;

/**
 * Filters the vectorial layers that are in edition
 * 
 * @author Fernando González Cortés
 * @author Víctor González Cortés
 */
public class EditingFilter implements LayerFilter {
	@Override
	public boolean accepts(Layer layer) {
		return layer.isVectorial() && layer.isEditing();
	}
}
