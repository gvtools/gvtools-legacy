/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 * Utility class to manage 3D geometries. Keeps a LineString field and an array
 * of Z.
 * 
 * @author jldominguez
 */
public class LineString3D {
	private LineString ls;
	private double[] zc;

	public LineString3D(LineString l, double[] z) {
		ls = l;
		zc = z;
	}

	public LineString getLs() {
		return ls;
	}

	public void setLs(LineString l) {
		ls = l;
	}

	public double[] getZc() {
		return zc;
	}

	public void setZc(double[] z) {
		zc = z;
	}

	public LineString3D createReverse() {
		double[] nz = null;

		if (zc != null) {
			nz = OracleSpatialUtils.reverseArray(zc);
		}

		Coordinate[] nc = OracleSpatialUtils.reverseCoordinateArray(ls
				.getCoordinates());
		GeometryFactory gf = new GeometryFactory();
		CoordinateArraySequence ncs = new CoordinateArraySequence(nc);
		LineString nls = null;

		if (ls instanceof LinearRing) {
			nls = new LinearRing(ncs, gf);
		} else {
			nls = new LineString(ncs, gf);
		}

		return new LineString3D(nls, nz);
	}
}
