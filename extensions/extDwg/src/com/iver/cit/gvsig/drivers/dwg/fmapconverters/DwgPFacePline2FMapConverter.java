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
* $Id: DwgPFacePline2FMapConverter.java 11060 2007-04-04 17:48:53Z azabala $
* $Log$
* Revision 1.1  2007-04-04 17:48:53  azabala
* first version in cvs after libDwg<->libFMap disacopling
*
*
*/
package com.iver.cit.gvsig.drivers.dwg.fmapconverters;

import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.FPolygon3D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.jdwglib.dwg.DwgObject;
import com.iver.cit.jdwglib.dwg.objects.DwgPFacePolyline;
import com.iver.cit.jdwglib.dwg.objects.DwgVertexPFaceFace;

public class DwgPFacePline2FMapConverter implements IDwg2FMap {

	public IGeometry toFMapGeometry(DwgObject entity, boolean is3DFile) {
		DwgPFacePolyline pline = (DwgPFacePolyline)entity;
		FGeometryCollection solution = null;
		IGeometry[] geometries = null;
		if(pline.getVertices() != null && pline.getFaces() != null){
			if(pline.getVertices().size() == 0 || pline.getFaces().size() == 0){
				return solution;
			}
			ArrayList geomList = new ArrayList();
			int numFaces = pline.getFaces().size();
			for(int i = 0; i < numFaces; i++){
				DwgVertexPFaceFace face = (DwgVertexPFaceFace) pline.getFaces().get(i);
				int[] verticesId = face.getVerticesidx();
				ArrayList pts = new ArrayList();
				boolean lastWasInvisible = true;
				for(int j = 0; j < DwgPFacePolyline.NUM_VERTEX_OF_FACE; j++){
					if(verticesId[j] > 0){
	                    if(lastWasInvisible){
	                        pts.clear();
	                        lastWasInvisible = false;
	                    }
	                    // the index is 1-based
	                    try{
	                    pts.add(pline.getVertices().get(verticesId[j] -1));
	                    }catch(Throwable t){
	                    	t.printStackTrace();
	                    }
	                    
	                } else if(verticesId[j] < 0 && !lastWasInvisible){
	                    lastWasInvisible = true;
	                    pts.add(pline.getVertices().get( (verticesId[j] * -1) -1));
	                }	
				}// for j
				
				FPolygon3D polygon = FMapUtil.ptsTo3DPolygon(pts);
				IGeometry geom = ShapeFactory.createGeometry(polygon);
				geomList.add(geom);
				
				
// if(!lastWasInvisible){
// if(vertex[nrV] < 0){
// line.addPoint(knot[-vertex[nrV] - 1]);
// } else{
// line.addPoint(knot[vertex[nrV] - 1]);
// }
// set.addDrawable(line);
// }
	           
			}// for i
			geometries = new IGeometry[geomList.size()];
			geomList.toArray(geometries);
			solution = new FGeometryCollection(geometries);
		}
		return solution;
		
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.jdwglib.dwg.IDwg2FMap#toFMapString(boolean)
	 */
	public String toFMapString(boolean is3DFile) {
		if(is3DFile)
			return "FPolyline3D";
		else
			return "FPolyline2D";
	}

}

