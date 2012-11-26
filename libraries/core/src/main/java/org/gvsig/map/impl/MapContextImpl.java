package org.gvsig.map.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.gvsig.layer.Layer;
import org.gvsig.map.ErrorListener;
import org.gvsig.map.MapContext;
import org.gvsig.persistence.generated.MapType;
import org.gvsig.units.Unit;
import org.gvsig.util.ProcessContext;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class MapContextImpl implements MapContext {
	@Override
	public Layer getRootLayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addErrorListener(ErrorListener errorListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getLayersError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CoordinateReferenceSystem getCRS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCRS(CoordinateReferenceSystem crs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackgroundColor(Color c) {
		// TODO Auto-generated method stub

	}

	@Override
	public Color getBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void setAreaUnits(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMapUnits(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public Unit getMapUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Unit getDistanceUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Unit getAreaUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(BufferedImage image, Graphics2D g,
			ProcessContext canceldraw, long scaleDenominator) {
		// TODO Auto-generated method stub
	}
}
