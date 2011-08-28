package com.iver.cit.gvsig.fmap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.print.attribute.PrintRequestAttributeSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.utiles.swing.threads.Cancellable;

public interface MapContextDrawer {

	public void setMapContext(MapContext mapContext);
	public void setViewPort(ViewPort viewPort);
	public void draw(FLayers root, BufferedImage image, Graphics2D g, Cancellable cancel,
			double scale) throws ReadDriverException;
	public void print(FLayers root, Graphics2D g, Cancellable cancel,
			double scale, PrintRequestAttributeSet properties) throws ReadDriverException;
	public void dispose();
	public void clean();
}
