package org.gvsig.map.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.gvsig.layer.Layer;
import org.gvsig.layer.impl.CompositeLayer;
import org.gvsig.map.ErrorListener;
import org.gvsig.map.MapContext;
import org.gvsig.persistence.generated.MapType;
import org.gvsig.units.Unit;
import org.gvsig.util.ProcessContext;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class MapContextImpl implements MapContext {
	private Unit mapUnits, areaUnits, distanceUnits;

	private Layer rootLayer;
	private List<ErrorListener> errorListeners;
	private Color backgroundColor;
	private CoordinateReferenceSystem crs;

	public MapContextImpl(Unit mapUnits, Unit distanceUnits, Unit areaUnits,
			CoordinateReferenceSystem crs) {
		this.mapUnits = mapUnits;
		this.areaUnits = areaUnits;
		this.distanceUnits = distanceUnits;
		this.crs = crs;
		this.backgroundColor = Color.white;
		this.rootLayer = new CompositeLayer();
		this.errorListeners = new ArrayList<ErrorListener>();
	}

	@Override
	public Layer getRootLayer() {
		return rootLayer;
	}

	@Override
	public void addErrorListener(ErrorListener errorListener) {
		errorListeners.add(errorListener);
	}

	@Override
	public List<String> getLayersError() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setXML(MapType mainMap) {
		// TODO Auto-generated method stub
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
	public void draw(BufferedImage image, Graphics2D g, long scaleDenominator,
			ProcessContext processContext) {
		getRootLayer().draw(image, g, scaleDenominator, processContext);
	}
}
