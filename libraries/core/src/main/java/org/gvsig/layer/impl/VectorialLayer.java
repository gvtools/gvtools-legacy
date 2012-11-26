package org.gvsig.layer.impl;

import org.gvsig.layer.Layer;
import org.gvsig.layer.filter.LayerFilter;

public class VectorialLayer extends AbstractLayer {
	private boolean editing, active;

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

	@Override
	public boolean isVectorial() {
		return true;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	/**
	 * Starts the edition of this layer. If the layer is already being edited,
	 * this method does nothing.
	 */
	public void startEdition() {
		editing = true;
	}

	/**
	 * Stops the edition of this layer. If the layer is not being edited, this
	 * method does nothing.
	 */
	public void stopEdition() {
		editing = false;
	}

	/**
	 * Activates this layer. If the layer is already active, this method does
	 * nothing.
	 */
	public void activate() {
		active = true;
	}

	/**
	 * Deactivates this layer. If the layer is not active, this method does
	 * nothing.
	 */
	public void deactivate() {
		active = false;
	}
}
