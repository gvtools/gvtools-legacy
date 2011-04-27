/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package com.iver.cit.gvsig.fmap.drivers;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;

/**
 * Interfaz base para implementar los drivers de raster.
 *
 * @author $author$
 */
public interface BasicFileRasterDriver {
	/**
	 * @see com.iver.cit.gvsig.fmap.layers.LayerOperations#getFullExtent()
	 */
	public Rectangle2D getFullExtent() throws DriverIOException;

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.LayerOperations#draw(java.awt.image.BufferedImage,
	 * 		ISymbol)
	 */
	public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort)
		throws DriverIOException;

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.RasterOperations#queryByPoint(com.iver.cit.gvsig.fmap.operations.QueriedPoint)
	 */
	public Object queryByPoint(Point point);
}