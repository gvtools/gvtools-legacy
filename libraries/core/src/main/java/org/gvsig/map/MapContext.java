package org.gvsig.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.gvsig.layer.Layer;
import org.gvsig.persistence.generated.MapType;
import org.gvsig.units.Unit;
import org.gvsig.util.ProcessContext;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public interface MapContext {

	/**
	 * Get the root of the layer tree
	 * 
	 * @return
	 */
	Layer getRootLayer();

	void addErrorListener(ErrorListener errorListener);

	List<String> getLayersError();

	/**
	 * Gets this map {@link CoordinateReferenceSystem}
	 * 
	 * @return
	 */
	CoordinateReferenceSystem getCRS();

	/**
	 * Set this map {@link CoordinateReferenceSystem}
	 * 
	 * @param crs
	 */
	void setCRS(CoordinateReferenceSystem crs);

	void setBackgroundColor(Color c);

	Color getBackgroundColor();

	MapType getXML();

	void setXML(MapType mainMap);

	void setDistanceUnits(Unit unit);

	void setAreaUnits(Unit unit);

	void setMapUnits(Unit unit);

	Unit getMapUnits();

	Unit getDistanceUnits();

	Unit getAreaUnits();

	void draw(BufferedImage image, Graphics2D g, ProcessContext canceldraw,
			long scaleDenominator);
}
