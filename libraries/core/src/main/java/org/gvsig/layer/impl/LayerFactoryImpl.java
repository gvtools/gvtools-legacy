package org.gvsig.layer.impl;

import org.gvsig.layer.Layer;
import org.gvsig.layer.LayerFactory;
import org.gvsig.layer.Source;

public class LayerFactoryImpl implements LayerFactory {

	@Override
	public Layer createLayer(Source source) {
		VectorialLayer ret = new VectorialLayer(source);

		return ret;
	}

}
