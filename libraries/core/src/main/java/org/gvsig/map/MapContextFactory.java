package org.gvsig.map;

import org.gvsig.units.Unit;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public interface MapContextFactory {

	MapContext createMapContext(Unit mapUnits, Unit distanceUnits,
			Unit areaUnits, CoordinateReferenceSystem crs);

}
