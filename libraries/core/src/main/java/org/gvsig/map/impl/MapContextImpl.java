package org.gvsig.map.impl;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.referencing.CRS;
import org.gvsig.layer.Layer;
import org.gvsig.layer.impl.CompositeLayer;
import org.gvsig.map.ErrorListener;
import org.gvsig.map.MapContext;
import org.gvsig.persistence.generated.MapType;
import org.gvsig.units.Unit;
import org.gvsig.util.ProcessContext;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.inject.Inject;

public class MapContextImpl implements MapContext {
	private static final Logger logger = Logger.getLogger(MapContextImpl.class);

	private Unit mapUnits, areaUnits, distanceUnits;

	private Layer rootLayer;
	private List<ErrorListener> errorListeners;
	private Color backgroundColor;
	private CoordinateReferenceSystem crs;

	@Inject
	private EventBus eventBus;

	public MapContextImpl() {
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
		if (crs == null) {
			try {
				crs = CRS.decode(DEFAULT_CRS_CODE);
			} catch (FactoryException e) {
				logger.error("Cannot obtain default CRS", e);
				eventBus.fireEvent(new ExceptionEvent(
						"Cannot obtain default CRS", e));
			}
		}
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
		if (backgroundColor == null) {
			backgroundColor = DEFAULT_BG_COLOR;
		}
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
		if (mapUnits == null) {
			mapUnits = DEFAULT_MAP_UNITS;
		}
		return mapUnits;
	}

	@Override
	public Unit getDistanceUnits() {
		if (distanceUnits == null) {
			distanceUnits = DEFAULT_DISTANCE_UNITS;
		}
		return distanceUnits;
	}

	@Override
	public Unit getAreaUnits() {
		if (areaUnits == null) {
			areaUnits = DEFAULT_AREA_UNITS;
		}
		return areaUnits;
	}

	@Override
	public void draw(BufferedImage image, Graphics2D g,
			ProcessContext canceldraw, long scaleDenominator) {
		// TODO Auto-generated method stub
	}
}
