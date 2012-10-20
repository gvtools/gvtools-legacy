/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package org.gvsig.jts.voronoi;

import java.util.List;

import org.gvsig.exceptions.BaseException;
import org.gvsig.topology.Messages;

import com.iver.utiles.swing.threads.CancellableProgressTask;

public class IncrementalRectBoundsVoronoiStrategy extends
		AbstractVoronoiStrategy {

	public List<TriangleFeature> createTin(VoronoiAndTinInputLyr inputLyr,
			boolean onlySelection, CancellableProgressTask progressMonitor)
			throws BaseException {
		// TODO gt: lost functionality: createTin
		throw new UnsupportedOperationException();
		// List<TriangleFeature> solution = new ArrayList<TriangleFeature>();
		// Polygon bbox = (Polygon) Voronoier.getThiessenBoundingBox(inputLyr,
		// onlySelection);
		// Coordinate[] coords = bbox.getCoordinates();
		// Quadrilateral quad = new Quadrilateral(coords[0], coords[1],
		// coords[2],
		// coords[3]);
		//
		// VoronoiLyrAdapter pointsList = new VoronoiLyrAdapter(inputLyr);
		//
		// MapTriangulationFactory triangulator = new
		// MapTriangulationFactory(quad, pointsList);
		// Map<TINTriangle, TINTriangle> triangleMap =
		// triangulator.getTriangleMap();
		// Iterator<TINTriangle> triangles = triangleMap.values().iterator();
		// int idx = 0;
		// while(triangles.hasNext()){
		// TINTriangle triangle = triangles.next();
		// Point2D ta = new Point2D.Double(triangle.p0.getOrdinate(0),
		// triangle.p0.getOrdinate(1));
		// Point2D tb = new Point2D.Double(triangle.p1.getOrdinate(0),
		// triangle.p1.getOrdinate(1));
		// Point2D tc = new Point2D.Double(triangle.p2.getOrdinate(0),
		// triangle.p2.getOrdinate(1));
		// FTriangle ftriangle = new FTriangle(ta, tb, tc);
		// Value fid = ValueFactory.createValue(idx);
		// Value[] values = new Value[] { fid };
		// TriangleFeature feature = new TriangleFeature(ftriangle,
		// values,
		// new UID().toString());
		// solution.add(feature);
		// idx++;
		// }//while
		// return solution;
	}

	public String getName() {
		return Messages
				.getText("Incremental_DT_based_in_a_rectangular_bounding_box");
	}

}
