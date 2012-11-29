package org.gvsig.map.impl;

import geomatico.events.EventBus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.gvsig.events.DrawingErrorEvent;
import org.gvsig.layer.Layer;
import org.gvsig.layer.LayerFactory;
import org.gvsig.map.MapContext;
import org.gvsig.persistence.generated.MapType;
import org.gvsig.units.Unit;
import org.gvsig.util.ProcessContext;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class MapContextImpl implements MapContext, RenderListener {
	private Unit mapUnits, areaUnits, distanceUnits;

	private Layer rootLayer;
	private Color backgroundColor;
	private CoordinateReferenceSystem crs;
	private EventBus eventBus;
	private LayerFactory layerFactory;

	public MapContextImpl(EventBus eventBus, LayerFactory layerFactory,
			Unit mapUnits, Unit distanceUnits, Unit areaUnits,
			CoordinateReferenceSystem crs) {
		this.eventBus = eventBus;
		this.layerFactory = layerFactory;
		this.mapUnits = mapUnits;
		this.areaUnits = areaUnits;
		this.distanceUnits = distanceUnits;
		this.crs = crs;
		this.backgroundColor = Color.white;
		this.rootLayer = layerFactory.createLayer();
	}

	@Override
	public Layer getRootLayer() {
		return rootLayer;
	}

	@Override
	public CoordinateReferenceSystem getCRS() {
		return crs;
	}

	@Override
	public void setCRS(CoordinateReferenceSystem crs) {
		this.crs = crs;
	}

	@Override
	public void setBackgroundColor(Color c) {
		this.backgroundColor = c;
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public MapType getXML() {
		MapType type = new MapType();
		type.setAreaUnits(areaUnits.ordinal());
		type.setMapUnits(mapUnits.ordinal());
		type.setDistanceUnits(distanceUnits.ordinal());
		type.setColor(backgroundColor.getRGB());
		type.setCrs(CRS.toSRS(crs));
		// type.setRootLayer(rootLayer.getXML());
		return type;
	}

	@Override
	public void setXML(MapType mainMap) {
		Unit[] units = Unit.values();
		mapUnits = units[mainMap.getMapUnits()];
		areaUnits = units[mainMap.getAreaUnits()];
		distanceUnits = units[mainMap.getDistanceUnits()];

		backgroundColor = new Color(mainMap.getColor());

		try {
			crs = CRS.decode(mainMap.getCrs());
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		rootLayer = layerFactory.createLayer();
		rootLayer.setXML(mainMap.getRootLayer());
	}

	@Override
	public void setDistanceUnits(Unit unit) {
		this.distanceUnits = unit;
	}

	@Override
	public void setAreaUnits(Unit unit) {
		this.areaUnits = unit;
	}

	@Override
	public void setMapUnits(Unit unit) {
		this.mapUnits = unit;
	}

	@Override
	public Unit getMapUnits() {
		return mapUnits;
	}

	@Override
	public Unit getDistanceUnits() {
		return distanceUnits;
	}

	@Override
	public Unit getAreaUnits() {
		return areaUnits;
	}

	@Override
	public void draw(BufferedImage image, Graphics2D g, Rectangle2D extent,
			long scaleDenominator, ProcessContext processContext)
			throws IOException {
		assert false : "processContext.isCancelled should be taken into account";

		GTRenderer renderer = new StreamingRenderer();

		MapContent mapContent = new MapContent();
		Collection<org.geotools.map.Layer> gtLayers;
		gtLayers = getRootLayer().getDrawingLayers();
		mapContent.addLayers(gtLayers);
		renderer.setMapContent(mapContent);
		renderer.addRenderListener(this);

		Rectangle imageArea = new Rectangle(0, 0, image.getWidth(),
				image.getHeight());
		Envelope mapArea = new Envelope(extent.getMinX(), extent.getMaxX(),
				extent.getMinY(), extent.getMaxY());
		renderer.paint(g, imageArea, mapArea);
	}

	private void reportError(Exception e, String message) {
		eventBus.fireEvent(new DrawingErrorEvent(this, message, e));
	}

	@Override
	public void featureRenderer(SimpleFeature feature) {
		// ignore
	}

	@Override
	public void errorOccurred(Exception e) {
		reportError(e, e.getMessage());
	}
}
