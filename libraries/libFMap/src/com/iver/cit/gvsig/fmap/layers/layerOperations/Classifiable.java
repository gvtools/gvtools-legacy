/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *   Av. Blasco Ib��ez, 50
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
package com.iver.cit.gvsig.fmap.layers.layerOperations;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.drivers.shp.write.SHPFileWrite;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.LegendListener;

/**
 * <p>
 * Interface that all layers that can be classifiable, must implement.
 * </p>
 * <p>
 * The interface <code>Classifiable</code> allows classify a layer in
 * <i>raster</i> or <i>vector</i>, and then, generates a legend.
 * </p>
 */
public interface Classifiable {
	/**
	 * Adds the specified legend listener to receive legend events from the
	 * inner legend. If the parameter is null, no exception is thrown and no
	 * action is performed.
	 * 
	 * @param listener
	 *            the legend listener
	 * 
	 * @see #removeLegendListener(LegendListener)
	 */
	void addLegendListener(LegendListener listener);

	/**
	 * Removes the specified legend listener so that it no longer receives
	 * legend events from the inner legend. This method performs no function,
	 * nor does it throw an exception, if the listener specified by the argument
	 * was not previously added to the legend. If the parameter is null, no
	 * exception is thrown and no action is performed.
	 * 
	 * @param listener
	 *            the legend listener
	 * 
	 * @see #addLegendListener(LegendListener)
	 */
	void removeLegendListener(LegendListener listener);

	/**
	 * Returns the inner legend.
	 * 
	 * @return Legend.
	 */
	public ILegend getLegend();

	/**
	 * <p>
	 * Returns the type of the shape.
	 * </p>
	 * <p>
	 * All geometry types are defined in <code>FShape</code>, and their
	 * equivalent shape in <code>FConstant</code>. For getting the equivalent
	 * shape of a geometry type, you must use a method like
	 * <code>{@linkplain SHPFileWrite#getShapeType()}</code>.
	 * </p>
	 * 
	 * @return the type of this shape.
	 * 
	 * @see FShape
	 * @see FConstant
	 * 
	 * @throws ReadDriverException
	 *             if the driver fails reading the data.
	 */
	public int getShapeType() throws ReadDriverException;
}
