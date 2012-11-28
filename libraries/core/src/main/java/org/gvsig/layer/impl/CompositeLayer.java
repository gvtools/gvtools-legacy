package org.gvsig.layer.impl;

import geomatico.events.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.styling.Style;
import org.gvsig.events.LayerAddedEvent;
import org.gvsig.layer.Layer;
import org.gvsig.layer.filter.LayerFilter;

public class CompositeLayer extends AbstractLayer {
	private List<Layer> layers = new ArrayList<Layer>();

	private EventBus eventBus;

	public CompositeLayer(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public boolean contains(Layer layer) {
		if (layer == this) {
			return true;
		} else {
			for (Layer l : layers) {
				if (l.contains(layer)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public Layer[] getAllLayers() {
		Layer[] ret = new Layer[layers.size() + 1];
		ret[0] = this;
		for (int i = 1; i < ret.length; i++) {
			ret[i] = layers.get(i - 1);
		}
		return ret;
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
		eventBus.fireEvent(new LayerAddedEvent(layer));
	}

	public boolean removeLayer(Layer layer) {
		return layers.remove(layer);
	}

	@Override
	public Style getStyle() {
		throw new UnsupportedOperationException(
				"Layer groups do not have style property");
	}

	@Override
	public void setStyle(Style style) {
		throw new UnsupportedOperationException(
				"Layer groups do not have style property");
	}

	@Override
	public Collection<org.geotools.map.Layer> getDrawingLayers()
			throws IOException {
		ArrayList<org.geotools.map.Layer> ret = new ArrayList<org.geotools.map.Layer>();
		for (Layer layer : this.layers) {
			ret.addAll(layer.getDrawingLayers());
		}

		return ret;
	}

	@Override
	public ReferencedEnvelope getBounds() throws IOException {
		ReferencedEnvelope ret = null;
		for (Layer layer : this.layers) {
			if (ret == null) {
				ret = new ReferencedEnvelope(layer.getBounds());
			} else {
				ret.expandToInclude(layer.getBounds());
			}
		}

		return ret;
	}
}