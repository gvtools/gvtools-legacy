package com.iver.cit.gvsig.geoprocess.impl.clean;

import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;

public interface ICleanGeoprocessUserEntries extends IGeoprocessUserEntries {
	
	public double getFuzzyTolerance() throws GeoprocessException;
	
	public double getDangleTolerance() throws GeoprocessException;
	
//	public String getOutputLayerType();
	
	public boolean createLyrsWithErrorGeometries();
	
	public boolean cleanOnlySelection();
}
