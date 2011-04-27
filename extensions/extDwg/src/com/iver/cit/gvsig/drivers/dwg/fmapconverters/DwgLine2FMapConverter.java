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
* $Id: DwgLine2FMapConverter.java 11060 2007-04-04 17:48:53Z azabala $
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
import com.iver.cit.jdwglib.dwg.objects.DwgLine;

public class DwgLine2FMapConverter implements IDwg2FMap {

	public IGeometry toFMapGeometry(DwgObject entity, boolean is3DFile) {
		DwgLine dwline = (DwgLine)entity;
		FPolyline2D line = null;
		double[] p1 = dwline.getP1();
		double[] p2 = dwline.getP2();
		if (is3DFile && dwline.isZflag()) {
			List lin3D = new ArrayList();
			lin3D.add(p1);
			lin3D.add(p2);
			line = FMapUtil.points3DToFPolyline3D(lin3D);
		} else if (is3DFile && ! dwline.isZflag()) {
			List lin3D = new ArrayList();
			p1[2] = 0d;
			p2[2] = 0d;
			lin3D.add(p1);
			lin3D.add(p2);
			line = FMapUtil.points3DToFPolyline3D(lin3D);
		} else {
			List lin = new ArrayList();
			lin.add(p1);
			lin.add(p2);
			line = FMapUtil.points2DToFPolyline2D(lin);
		}
		return ShapeFactory.createGeometry(line);
		
	}
	public String toFMapString(boolean is3DFile) {
		if(is3DFile){
			return "FPolyline3D";
		}else{
			return "FPolyline2D";
		}
	}

}

