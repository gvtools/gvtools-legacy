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

import java.awt.geom.Rectangle2D;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.IGeometry;

/**
 * Clase padre de los drivers vectoriales
 */
public interface VectorialDriver extends Driver {
	/**
	 * Devuelve el tipo de los shapes que hay en el fichero. Pueden haber varios
	 * tipos de shapes y para indicar esto se devuelve un bitoring de los tipos
	 * que contiene el fichero. Por ejemplo un fichero que contenga textos y
	 * lineas devolverá la expresión (FShape.LINE | FShape.TEXT)
	 * 
	 * @return tipo de shape.
	 */
	int getShapeType();

	/**
	 * Obtiene el número de geometrías que contiene la capa
	 * 
	 * @return int
	 * @throws ReadDriverException
	 *             TODO
	 */
	int getShapeCount() throws ReadDriverException;

	/**
	 * Devuelve los atributos que necesitemos conocer de un driver. Por ejemplo,
	 * si se carga en memoria, o cualquier otra cosa que necesitemos. Pasa a
	 * través del adapter para que una FLayer pueda conocer ciertos aspectos del
	 * driver que le está entregando las features.
	 * 
	 * @return
	 */
	DriverAttributes getDriverAttributes();

	/**
	 * Obtiene el bounding box de la capa
	 * 
	 * @return Rectangle2D
	 * @throws ReadDriverException
	 *             TODO
	 */
	Rectangle2D getFullExtent() throws ReadDriverException,
			ExpansionFileReadException;

	public IGeometry getShape(int index) throws ReadDriverException;

	void reload() throws ReloadDriverException;

	boolean isWritable();
}
