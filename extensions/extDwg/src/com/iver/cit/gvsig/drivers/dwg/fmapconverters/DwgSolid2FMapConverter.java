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
* $Id: DwgSolid2FMapConverter.java 11060 2007-04-04 17:48:53Z azabala $
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
import com.iver.cit.jdwglib.dwg.objects.DwgSolid;

public class DwgSolid2FMapConverter implements IDwg2FMap {

	public IGeometry toFMapGeometry(DwgObject entity, boolean is3DFile) {
		DwgSolid dwsolid = (DwgSolid)entity;
		FPolyline2D solid = null;
		double[] p1 = dwsolid.getCorner1();
		double[] p2 = dwsolid.getCorner2();
		double[] p3 = dwsolid.getCorner3();
		double[] p4 = dwsolid.getCorner4();
		double elev = dwsolid.getElevation();
		List pts = new ArrayList();
		
		if (is3DFile) {
			double[] p13d = new double[]{p1[0], p1[1], elev};
			double[] p23d = new double[]{p2[0], p2[1], elev};
			double[] p33d = new double[]{p3[0], p3[1], elev};
			double[] p43d = new double[]{p4[0], p4[1], elev};
			pts.add(p13d);
			pts.add(p23d);
			pts.add(p33d);
			pts.add(p43d);

			solid = FMapUtil.ptsTo3DPolygon(pts);
			
			
		} else {
			pts.add(p1);
			pts.add(p2);
			pts.add(p3);
			pts.add(p4);
			solid = FMapUtil.ptsTo2DPolygon(pts);
		}
		return ShapeFactory.createGeometry(solid);
		
	}
	public String toFMapString(boolean is3DFile) {
		if(is3DFile){
			return "FPolyline3D";
		}else{
			return "FPolyline2D";
		}
	}

}

