/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */

/* CVS MESSAGES:
 *
 * $Id: ILabelable.java 24273 2008-10-24 10:32:42Z vcaballero $
 * $Log$
 * Revision 1.2  2007-03-09 11:20:57  jaume
 * Advanced symbology (start committing)
 *
 * Revision 1.1.2.5  2007/02/15 16:23:44  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.4  2007/02/09 07:47:05  jaume
 * Isymbol moved
 *
 * Revision 1.1.2.3  2007/02/02 16:21:24  jaume
 * start commiting labeling stuff
 *
 * Revision 1.1.2.2  2007/02/01 17:46:49  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.1  2007/01/30 18:10:45  jaume
 * start commiting labeling stuff
 *
 *
 */
package com.iver.cit.gvsig.fmap.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.print.attribute.PrintRequestAttributeSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * This interface enables support for labeling an object such as a layer, but
 * also others, if any. It gives support for detecting if labeling is being
 * applied (<code>isLabeled()</code> and <code>setIsLabeled()</code>), to manage
 * the labeling strategy (<code>getLabelingStrategy()</code> and
 * <code>setLabelingStrategy</code>). The labeling is performed by the
 * <code>drawLabels(..)</code> method.
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public interface ILabelable {
	/**
	 * Returns <b>true</b> if labels are drawn, or <b>false</b> otherwise.
	 * 
	 * @return <b>boolean</b> telling if labels are drawn
	 */
	public boolean isLabeled();

	/**
	 * Enables or disables the label drawing.
	 * 
	 * @param isLabeled
	 *            , if true then labels will be drawn
	 */
	public void setIsLabeled(boolean isLabeled);

	/**
	 * Returns the current labeling strategy
	 * 
	 * @return ILabelingStrategy
	 * @see ILabelingStrategy
	 */
	public ILabelingStrategy getLabelingStrategy();

	/**
	 * Sets the new labeling strategy. Changes on the results will take effect
	 * next time the drawLabels(...) method is invoked.
	 * 
	 * @param strategy
	 * @uml.property name="labelingStrategy"
	 */
	public void setLabelingStrategy(ILabelingStrategy strategy);

	/**
	 * Causes the labels to be drawn. The policy of process is determined by the
	 * LabelingStrategy previously set.
	 * 
	 * @param image
	 * @param g
	 * @param viewPort
	 * @param cancel
	 * @param scale
	 * @param dpi
	 * @throws DriverException
	 */
	public void drawLabels(BufferedImage image, Graphics2D g,
			ViewPort viewPort, Cancellable cancel, double scale, double dpi)
			throws ReadDriverException;

	public void printLabels(Graphics2D g, ViewPort viewPort,
			Cancellable cancel, double scale,
			PrintRequestAttributeSet properties) throws ReadDriverException;
}
