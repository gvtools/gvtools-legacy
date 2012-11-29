package org.gvsig.layer.impl;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.gvsig.layer.FeatureSourceCache;
import org.gvsig.layer.Layer;
import org.gvsig.layer.Source;
import org.gvsig.layer.SourceFactory;
import org.gvsig.layer.SymbolFactoryFacade;
import org.gvsig.layer.filter.LayerFilter;
import org.gvsig.persistence.generated.LayerType;

public class VectorialLayer implements Layer {
	private boolean editing, active;
	private Source source;
	private Style style;

	private SourceFactory sourceFactory;
	private FeatureSourceCache sourceManager;
	private SymbolFactoryFacade symbolFactoryFacade;

	public VectorialLayer(FeatureSourceCache featureSourceCache,
			SymbolFactoryFacade symbolFactoryFacade,
			SourceFactory sourceFactory, Source source) {
		this.sourceManager = featureSourceCache;
		this.symbolFactoryFacade = symbolFactoryFacade;
		this.sourceFactory = sourceFactory;
		this.source = source;
	}

	@Override
	public boolean contains(Layer layer) {
		return this == layer;
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

	@Override
	public void setStyle(Style style) {
		this.style = style;
	}

	@Override
	public Style getStyle() {
		if (style == null) {
			style = symbolFactoryFacade.newLineStyle(Color.BLUE, 1);
		}

		return style;
	}

	@Override
	public Collection<org.geotools.map.Layer> getDrawingLayers()
			throws IOException {
		return Collections.singletonList(getGTLayer());
	}

	private org.geotools.map.Layer getGTLayer() throws IOException {
		SimpleFeatureSource featureSource = sourceManager
				.getFeatureSource(source);
		org.geotools.map.Layer layer = new FeatureLayer(featureSource,
				getStyle());
		return layer;
	}

	@Override
	public void addLayer(Layer testLayer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ReferencedEnvelope getBounds() throws IOException {
		return getGTLayer().getBounds();
	}

	@Override
	public boolean removeLayer(Layer layer) {
		// TODO Auto-generated method stub
		assert false : "TODO";
		return false;
	}

	@Override
	public LayerType getXML() {
		LayerType xml = new LayerType();
		xml.setActive(isActive());
		xml.setEditing(isEditing());
		xml.setVectorial(isVectorial());
		xml.setSource(source.getXML());
		return xml;
	}

	@Override
	public void setXML(LayerType layer) {
		if (!layer.isVectorial()) {
			throw new IllegalArgumentException("Attempting to assign a "
					+ "non-vectorial layer to a vectorial layer");
		} else if (layer.getLayers().size() > 0) {
			throw new IllegalArgumentException("Attempting to assign a "
					+ "layer with children to a vectorial layer");
		}

		active = layer.isActive();
		editing = layer.isEditing();
		source = sourceFactory.createSource(layer.getSource());
	}
}
