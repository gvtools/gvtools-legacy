package org.gvsig.layer.impl;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.gvsig.layer.Layer;
import org.gvsig.layer.Source;
import org.gvsig.layer.SourceManager;
import org.gvsig.layer.SymbolFactoryFacade;
import org.gvsig.layer.filter.LayerFilter;

public class VectorialLayer extends AbstractLayer {
	private boolean editing, active;
	private Source source;
	private Style style;

	@Inject
	private SourceManager sourceManager;

	@Inject
	private SymbolFactoryFacade symbolFactoryFacade;

	public VectorialLayer(Source source) {
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
}
