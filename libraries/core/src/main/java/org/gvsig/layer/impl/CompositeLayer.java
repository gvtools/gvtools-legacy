package org.gvsig.layer.impl;

import java.util.ArrayList;
import java.util.List;

import org.gvsig.layer.Layer;
import org.gvsig.layer.filter.LayerFilter;

public class CompositeLayer extends AbstractLayer {
	private List<Layer> layers = new ArrayList<Layer>();

	@Override
	public boolean contains(Layer layer) {
		for (Layer l : layers) {
			if (l.equals(layer)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Layer[] getAllLayers() {
		return layers.toArray(new Layer[layers.size()]);
	}

	@Override
	public Layer[] filter(LayerFilter filter) {
		List<Layer> ret = new ArrayList<Layer>();
		for (Layer layer : layers) {
			if (filter.accepts(layer)) {
				ret.add(layer);
			}
		}
		return ret.toArray(new Layer[ret.size()]);
	}

	@Override
	public boolean isEditing() {
		return false;
	}

	@Override
	public boolean isVectorial() {
		return false;
	}
	
	@Override
	public boolean isActive() {
		return false;
	}

	public void addLayer(Layer layer) {
		if (layer == null) {
			throw new IllegalArgumentException("Layer cannot be null");
		}
		layers.add(layer);
	}

	public boolean removeLayer(Layer layer) {
		return layers.remove(layer);
	}
}
