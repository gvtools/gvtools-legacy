package org.gvsig.map.impl;

import org.gvsig.inject.LibModule;
import org.gvsig.map.MapContext;
import org.gvsig.map.MapContextFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MapContextFactoryImpl implements MapContextFactory {
	private static final Injector injector = Guice
			.createInjector(new LibModule());

	@Override
	public MapContext createMapContext() {
		return injector.getInstance(MapContextImpl.class);
	}
}
