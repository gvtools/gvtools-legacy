package org.gvsig.map.impl;

import geomatico.events.EventBus;

import javax.inject.Inject;

import org.gvsig.layer.LayerFactory;
import org.gvsig.map.MapContext;
import org.gvsig.map.MapContextFactory;
import org.gvsig.units.Unit;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.inject.Provider;

public class MapContextFactoryImpl implements MapContextFactory {

	@Inject
	private Provider<EventBus> eventBusProvider;

	@Inject
	private Provider<LayerFactory> layerFactoryProvider;

	@Override
	public MapContext createMapContext(Unit mapUnits, Unit distanceUnits,
			Unit areaUnits, CoordinateReferenceSystem crs) {
		return new MapContextImpl(eventBusProvider.get(),
				layerFactoryProvider.get(), mapUnits, distanceUnits, areaUnits,
				crs);
	}
}
