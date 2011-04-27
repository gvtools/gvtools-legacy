package com.iver.cit.gvsig.geoprocess.impl.build;

import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;

public interface IBuildGeoprocessUserEntries extends IGeoprocessUserEntries {

	public boolean createLyrsWithErrorGeometries();
	
	public boolean buildOnlySelection();
	
	public boolean cleanBefore();
	
	public double getFuzzyTolerance() throws GeoprocessException;
	
	public double getDangleTolerance() throws GeoprocessException;
	
}
