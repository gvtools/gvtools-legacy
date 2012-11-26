package org.gvsig.inject;

import geomatico.events.EventBus;

import org.gvsig.map.MapContextFactory;
import org.gvsig.map.impl.MapContextFactoryImpl;

import com.google.inject.Provider;
import com.iver.cit.gvsig.fmap.MapControl;

public class ExtModule extends LibModule {

	@Override
	protected void configure() {
		super.configure();
		bind(EventBus.class).toInstance(EventBus.getInstance());
		bind(MapContextFactory.class).to(MapContextFactoryImpl.class);
		bind(MapControl.class).toProvider(new Provider<MapControl>() {
			@Override
			public MapControl get() {
				MapControl control = new MapControl();
				InjectorSingleton.getInjector().injectMembers(control);
				control.init();
				return control;
			}
		});
	}
}
