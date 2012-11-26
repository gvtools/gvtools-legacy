package org.gvsig.inject;

import com.google.inject.Provider;
import com.iver.cit.gvsig.fmap.MapControl;

public class ExtModule extends LibModule {

	@Override
	protected void configure() {
		super.configure();
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
