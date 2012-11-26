package org.gvsig.map.impl;

import org.gvsig.map.MapContext;
import org.gvsig.map.MapContextFactory;

public class MapContextFactoryImpl implements MapContextFactory {
	@Override
	public MapContext createMapContext() {
		return new MapContextImpl();
	}
}
