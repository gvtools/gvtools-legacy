package org.gvsig.layer.impl;

import org.gvsig.layer.Layer;

public abstract class AbstractLayer implements Layer {
	private String name;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
