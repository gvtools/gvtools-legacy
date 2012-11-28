package org.gvsig.layer;

public interface LayerFactory {

	Layer createLayer(Source source);

	Layer createLayer(Layer... layers);
}
