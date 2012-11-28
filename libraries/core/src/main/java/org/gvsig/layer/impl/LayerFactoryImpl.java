package org.gvsig.layer.impl;

import geomatico.events.EventBus;

import javax.inject.Inject;
import org.gvsig.layer.Layer;
import org.gvsig.layer.LayerFactory;
import org.gvsig.layer.Source;
import org.gvsig.layer.SourceManager;
import org.gvsig.layer.SymbolFactoryFacade;

import com.google.inject.Provider;

public class LayerFactoryImpl implements LayerFactory {

	@Inject
	private Provider<SourceManager> sourceManagerProvider;

	@Inject
	private Provider<SymbolFactoryFacade> symbolFactoryFacadeProvider;

	@Inject
	private Provider<EventBus> eventBusProvider;

	@Override
	public Layer createLayer(Source source) {
		VectorialLayer ret = new VectorialLayer(sourceManagerProvider.get(),
				symbolFactoryFacadeProvider.get(), source);

		return ret;
	}

	@Override
	public Layer createLayer(Layer... layers) {
		CompositeLayer composite = new CompositeLayer(eventBusProvider.get());
		for (Layer layer : layers) {
			composite.addLayer(layer);
		}
		return composite;
	}

}
