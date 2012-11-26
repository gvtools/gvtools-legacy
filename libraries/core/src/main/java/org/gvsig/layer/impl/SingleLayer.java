package org.gvsig.layer.impl;

import org.gvsig.layer.Layer;
import org.gvsig.layer.LayerFilter;

public class SingleLayer extends AbstractLayer {
	private boolean editing;

	@Override
	public boolean contains(Layer layer) {
		return this.equals(layer);
	}

	@Override
	public Layer[] getAllLayers() {
		return new Layer[] { this };
	}

	@Override
	public Layer[] filter(LayerFilter filter) {
		if (filter.accepts(this)) {
			return new Layer[] { this };
		} else {
			return new Layer[0];
		}
	}

	@Override
	public boolean isEditing() {
		return editing;
	}

	public void startEdition() {
		editing = true;
	}

	public void stopEdition() {
		editing = false;
	}
}
