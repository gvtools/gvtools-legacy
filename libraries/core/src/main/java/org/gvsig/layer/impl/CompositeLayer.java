package org.gvsig.layer.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.geotools.styling.Style;
import org.gvsig.layer.Layer;
import org.gvsig.layer.filter.LayerFilter;
import org.gvsig.util.ProcessContext;

public class CompositeLayer extends AbstractLayer {
	private List<Layer> layers = new ArrayList<Layer>();

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
	}

	public boolean removeLayer(Layer layer) {
		return layers.remove(layer);
	}

	@Override
	public void draw(BufferedImage image, Graphics2D g, long scaleDenominator,
			ProcessContext processContext) {
		for (int i = layers.size() - 1; i >= 0; i--) {
			Layer layer = layers.get(i);
			layer.draw(image, g, scaleDenominator, processContext);
		}
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
}