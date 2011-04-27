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
package com.iver.cit.gvsig.fmap.operations.strategies;

import java.awt.geom.Rectangle2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;


/**
 * Visitor de zoom a lo seleccionado.
 *
 * @author Vicente Caballero Navarro
 */
public class SelectedZoomVisitor implements FeatureVisitor {
	private Rectangle2D rectangle = null;
	private FBitSet bitset = null;

	/**
	 * Inicializa el visitor.
	 *
	 * @param layer Capa.
	 *
	 * @return True si se inicializa correctamente.
	 */
	public boolean start(FLayer layer) throws StartVisitorException {
		if (layer instanceof AlphanumericData) {//TODO Esta comprobación se hacia con Selectable
			try {
				bitset = ((AlphanumericData) layer).getRecordset().getSelection();
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}

			return true;
		}

		return false;
	}

	/**
	 * Finaliza el visitor.
	 *
	 * @param layer Capa.
	 */
	public void stop(FLayer layer) throws VisitorException {
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor#visit(com.iver.cit.gvsig.fmap.core.IGeometry,
	 * 		int)
	 */
	public void visit(IGeometry g, int index) throws VisitorException, ProcessVisitorException {
		if (bitset.get(index)) {
			if (rectangle == null) {
				rectangle = (Rectangle2D) g.getBounds2D().clone();
			} else {
				rectangle.add(g.getBounds2D());
			}
		}
	}

	/**
	 * Devuelve el Extent de los shapes seleccionados, y si no hay ningún shape
	 * seleccionado devuelve null.
	 *
	 * @return Extent de los shapes seleccionados.
	 */
	public Rectangle2D getSelectBound() {
		return rectangle;
	}

	public String getProcessDescription() {
		return "Defining rectangle to zoom from selected geometries";
	}
}
