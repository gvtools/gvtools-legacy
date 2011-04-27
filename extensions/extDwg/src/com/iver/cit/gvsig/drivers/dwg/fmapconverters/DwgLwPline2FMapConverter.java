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
* $Id: DwgLwPline2FMapConverter.java 11060 2007-04-04 17:48:53Z azabala $
* $Log$
* Revision 1.1  2007-04-04 17:48:53  azabala
* first version in cvs after libDwg<->libFMap disacopling
*
*
*/
package com.iver.cit.gvsig.drivers.dwg.fmapconverters;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.jdwglib.dwg.DwgObject;
import com.iver.cit.jdwglib.dwg.objects.DwgLwPolyline;

public class DwgLwPline2FMapConverter implements IDwg2FMap {

	public IGeometry toFMapGeometry(DwgObject entity, boolean is3DFile) {
		DwgLwPolyline pline = (DwgLwPolyline)entity;
		FPolyline2D lwpline = null;
		List pts = pline.getVertices();
		double elev = pline.getElevation();
		if (pts != null && pts.size() > 0) {
			
			if (is3DFile) {
				List pline3D = new ArrayList();
				for (int j = 0; j < pts.size(); j++) {
					Object vertex = pts.get(j);
					double[] pt = new double[3];
					if(vertex instanceof double[]){
						double[] vertexArray = (double[])vertex;
						pt[0] = vertexArray[0];
						pt[1] = vertexArray[1];
					}else if(vertex instanceof Point2D){
						Point2D vertexPt = (Point2D)vertex;
						pt[0] = vertexPt.getX();
						pt[1] = vertexPt.getY();
					}
					pt[2] = elev;
					pline3D.add(pt);
				}
				lwpline = FMapUtil.points3DToFPolyline3D(pline3D);
			} else {
				lwpline = FMapUtil.points2DToFPolyline2D(pts);
			}
			return ShapeFactory.createGeometry(lwpline);
		}
		return null;
		
	}
	public String toFMapString(boolean is3DFile) {
		if(is3DFile)
			return "FPolyline3D";
		else
			return "FPolyline2D";
	}

}

