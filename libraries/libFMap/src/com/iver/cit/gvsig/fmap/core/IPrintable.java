package com.iver.cit.gvsig.fmap.core;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.print.attribute.PrintRequestAttributeSet;

public interface IPrintable {
	public void print(Graphics2D g, AffineTransform at, FShape shape,
			PrintRequestAttributeSet properties);
}
