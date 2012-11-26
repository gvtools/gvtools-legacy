package org.gvsig.inject;

import geomatico.events.EventBus;

import org.gvsig.layer.SourceFactory;
import org.gvsig.layer.SourceManager;
import org.gvsig.layer.impl.SourceFactoryImpl;
import org.gvsig.layer.impl.SourceManagerImpl;
import org.gvsig.map.MapContextFactory;
import org.gvsig.map.impl.MapContextFactoryImpl;

import com.google.inject.AbstractModule;

public class LibModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SourceManager.class).to(SourceManagerImpl.class);
		bind(SourceFactory.class).to(SourceFactoryImpl.class);
		bind(MapContextFactory.class).to(MapContextFactoryImpl.class);
		bind(EventBus.class).toInstance(EventBus.getInstance());
	}
}
