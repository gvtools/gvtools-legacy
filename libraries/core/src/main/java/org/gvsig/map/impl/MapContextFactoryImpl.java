package org.gvsig.map.impl;

import org.gvsig.map.MapContext;
import org.gvsig.map.MapContextFactory;
import org.gvsig.units.Unit;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class MapContextFactoryImpl implements MapContextFactory {

	@Override
	public MapContext createMapContext(Unit mapUnits, Unit distanceUnits,
			Unit areaUnits, CoordinateReferenceSystem crs) {
		return new MapContextImpl(mapUnits, distanceUnits, areaUnits, crs);
	}
}
