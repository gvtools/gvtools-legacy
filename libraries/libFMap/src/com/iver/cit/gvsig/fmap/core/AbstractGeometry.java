package com.iver.cit.gvsig.fmap.core;

import java.awt.Graphics2D;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;

public abstract class AbstractGeometry implements IGeometry {

	public void drawInts(Graphics2D g, ViewPort vp, ISymbol symbol) {
		drawInts(g, vp, symbol, null);
	}

	public void draw(Graphics2D g, ViewPort vp, ISymbol symbol) {
		drawInts(g, vp, symbol, null);
	}
}
