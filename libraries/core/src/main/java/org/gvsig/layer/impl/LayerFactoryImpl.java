package org.gvsig.layer.impl;

import geomatico.events.EventBus;

import java.util.List;

import javax.inject.Inject;

import org.gvsig.layer.Layer;
import org.gvsig.layer.LayerFactory;
import org.gvsig.layer.Source;
import org.gvsig.layer.SourceFactory;
import org.gvsig.layer.FeatureSourceCache;
import org.gvsig.layer.SymbolFactoryFacade;
import org.gvsig.persistence.generated.LayerType;

import com.google.inject.Provider;

public class LayerFactoryImpl implements LayerFactory {
	@Inject
	private Provider<FeatureSourceCache> featureSourceCache;

	@Inject
	private Provider<SymbolFactoryFacade> symbolFactoryFacadeProvider;

	@Inject
	private Provider<SourceFactory> sourceFactoryProvider;

	@Inject
	private Provider<EventBus> eventBusProvider;

	@Override
	public Layer createLayer(Source source) {
		VectorialLayer ret = new VectorialLayer(featureSourceCache.get(),
				symbolFactoryFacadeProvider.get(), sourceFactoryProvider.get(),
				source);

		return ret;
	}

	@Override
	public Layer createLayer(Layer... layers) {
		CompositeLayer composite = new CompositeLayer(eventBusProvider.get(),
				this);
		for (Layer layer : layers) {
			composite.addLayer(layer);
		}
		return composite;
	}

	@Override
	public Layer createLayer(LayerType xml) {
		if (xml.isVectorial()) {
			Source source = sourceFactoryProvider.get().createSource(
					xml.getSource());
			return createLayer(source);
		} else {
			List<LayerType> xmlLayers = xml.getLayers();
			Layer[] children = new Layer[xmlLayers.size()];
			for (int i = 0; i < children.length; i++) {
				children[i] = createLayer(xmlLayers.get(i));
			}

			return createLayer(children);
		}
	}

}
