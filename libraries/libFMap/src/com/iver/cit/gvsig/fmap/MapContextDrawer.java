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


	/**
	 * Alternative draw method, used when DPI in real world is different,
	 * for example, when you create a map and zoom in or out in it.
	 * The method takes in consideration the "physical_dpi" especailly
	 * to draw fonts in correct size.
	 * 
	 * @param root
	 * @param image
	 * @param g
	 * @param cancel
	 * @param scale
	 * @param physical_dpi
	 * @throws ReadDriverException
	 */
	public void draw(FLayers root, BufferedImage image, Graphics2D g, Cancellable cancel,
			double scale, double physical_dpi) throws ReadDriverException;
	public void print(FLayers root, Graphics2D g, Cancellable cancel,
			double scale, PrintRequestAttributeSet properties) throws ReadDriverException;
	public void dispose();
	public void clean();
}
