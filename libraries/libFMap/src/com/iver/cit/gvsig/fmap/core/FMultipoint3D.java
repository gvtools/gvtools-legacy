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
package com.iver.cit.gvsig.fmap.core;

import java.awt.geom.Point2D;

/**
 * Multipunto 3D.
 *
 * @author Vicente Caballero Navarro
 */
public class FMultipoint3D extends FMultiPoint2D implements IGeometry3D {
	double[] z = null;

	/**
	 * Crea un nuevo Multipoint3D.
	 *
	 * @param x Array de Xs.
	 * @param y Array de Ys.
	 * @param z Array de Zs.
	 */
	public FMultipoint3D(double[] x, double[] y, double[] z) {
		super(x, y);
		this.z = z;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#cloneGeometry()
	 */
	public IGeometry cloneGeometry() {
		double[] x = new double[getNumPoints()];
		double[] y = new double[getNumPoints()];
		for (int i=0; i < getNumPoints(); i++)
		{
			Point2D p=points[i].getHandlers(IGeometry.SELECTHANDLER)[0].getPoint();

			x[i] = p.getX();
			y[i] = p.getY();
		}
		return new FMultipoint3D(x,y,
			(double[]) z.clone());
	}

	/**
	 * Devuelve un array con todos los valores de Z.
	 *
	 * @return Array de Zs.
	 */
	public double[] getZs() {
		return z;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getGeometryType()
	 */
	public int getGeometryType() {
		return FShape.MULTIPOINT | FShape.Z;
	}
}
