package org.gvsig.layer.impl;

import geomatico.events.EventBus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.inject.Inject;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.gvsig.events.LayerDrawErrorEvent;
import org.gvsig.layer.Layer;
import org.gvsig.layer.Source;
import org.gvsig.layer.SourceManager;
import org.gvsig.layer.SymbolFactoryFacade;
import org.gvsig.layer.filter.LayerFilter;
import org.gvsig.util.ProcessContext;
import org.opengis.feature.simple.SimpleFeature;

public class VectorialLayer extends AbstractLayer implements RenderListener {
	private boolean editing, active;
	private Source source;
	private Style style;

	@Inject
	private EventBus eventBus;

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
	public void draw(BufferedImage image, Graphics2D g, long scaleDenominator,
			ProcessContext processContext) {
		assert false : "processContext.isCancelled should be taken into account";
		GTRenderer renderer = new StreamingRenderer();
		MapContent mapContent = new MapContent();

		SimpleFeatureSource featureSource = null;
		try {
			featureSource = sourceManager.getFeatureSource(source);
		} catch (IOException e) {
			reportError(e, "Cannot instantiate feature source", this);
			return;
		}
		FeatureLayer layer = new FeatureLayer(featureSource, getStyle());
		mapContent.addLayer(layer);
		renderer.setMapContent(mapContent);

		ReferencedEnvelope bounds = null;
		try {
			bounds = featureSource.getBounds();
		} catch (IOException e) {
			reportError(e, "Cannot get layer bounds", this);
			return;
		}
		renderer.addRenderListener(this);
		if (bounds != null) {
			renderer.paint(g,
					new Rectangle(0, 0, image.getWidth(), image.getHeight()),
					bounds);
		}
	}

	private void reportError(Exception e, String message,
			VectorialLayer sourceLayer) {
		eventBus.fireEvent(new LayerDrawErrorEvent(sourceLayer, message, e));
	}

	@Override
	public void featureRenderer(SimpleFeature feature) {
		// ignore
	}

	@Override
	public void errorOccurred(Exception e) {
		reportError(e, e.getMessage(), this);
	}

	@Override
	public void addLayer(Layer testLayer) {
		throw new UnsupportedOperationException();
	}
}
