package org.gvsig.map;

import java.awt.Color;
import java.util.List;

import org.gvsig.layer.Layer;
import org.gvsig.persistence.generated.MapType;
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

	void setBackColor(Color c);

	MapType getXML();

}
