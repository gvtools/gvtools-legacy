/*
 * Created on 02/04/2007
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
*
* $Id: DwgArc2FMapConverter.java 11060 2007-04-04 17:48:53Z azabala $
* $Log$
* Revision 1.1  2007-04-04 17:48:53  azabala
* first version in cvs after libDwg<->libFMap disacopling
*
*
*/
package com.iver.cit.gvsig.drivers.dwg.fmapconverters;

import java.util.ArrayList;
import java.util.List;

import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.jdwglib.dwg.DwgObject;
import com.iver.cit.jdwglib.dwg.objects.DwgArc;
import com.iver.cit.jdwglib.util.GisModelCurveCalculator;

public class DwgArc2FMapConverter implements IDwg2FMap {

	/* (non-Javadoc)
	 * @see com.iver.cit.jdwglib.dwg.IDwg2FMap#toFMapGeometry()
	 */
	public IGeometry toFMapGeometry(DwgObject entity, boolean is3DFile) {
		DwgArc arc = (DwgArc) entity;
		double[] center = arc.getCenter();
		double radius = arc.getRadius();
		double initAngle = Math.toDegrees(arc.getInitAngle());
		double endAngle = Math.toDegrees(arc.getEndAngle());
		List arcs = GisModelCurveCalculator.calculateGisModelArc(
				center, radius, initAngle, endAngle);
		FPolyline2D arcc;
		if (is3DFile) {
			List arc3D = new ArrayList();
			for (int j = 0; j < arcs.size(); j++) {
				double[] point = (double[]) arcs.get(j);
				double[] newP = new double[]{point[0], point[1], center[2]};
				arc3D.add(newP);
			}
			arcc = FMapUtil.points3DToFPolyline3D(arc3D);
			
		} else {
			arcc = FMapUtil.points2DToFPolyline2D(arcs);
		}
		return ShapeFactory.createGeometry(arcc);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.jdwglib.dwg.IDwg2FMap#toFMapString()
	 */
	public String toFMapString(boolean is3dFile) {
		if(is3dFile)
			return "FPolyline3D";
		else
			return "FPolyline2D";
	}

}

