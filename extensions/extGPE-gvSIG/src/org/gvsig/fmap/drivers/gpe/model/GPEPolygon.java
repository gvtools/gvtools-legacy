/* gvSIG. Geographic Information System of the Valencian Government
*
* Copyright (C) 2007-2008 Infrastructures and Transports Department
* of the Valencian Government (CIT)
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
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
* MA  02110-1301, USA.
* 
*/

/*
* AUTHORS (In addition to CIT):
* 2009 Iver T.I.  {{Task}}
*/
 
package org.gvsig.fmap.drivers.gpe.model;

import java.awt.geom.PathIterator;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;

public class GPEPolygon extends GPEGeometry {
	private ArrayList innerPolygons = null;
	private GeneralPathX gp = null;
	
	public GPEPolygon(String id, GeneralPathX gp, String srs) {
		super(id, null, srs);		
		innerPolygons = new ArrayList();
		this.gp = gp;
	}
	
	/**
	 * Add a inner polygon to one polygon
	 * @param polygon
	 */
	public void addInnerPolygon(GPEPolygon polygon){
		innerPolygons.add(polygon);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.drivers.gpe.model.GPEGeometry#getIGeometry()
	 */
	public IGeometry getIGeometry() {
		if (geometry == null){
			geometry = ShapeFactory.createPolygon2D(createGeneralXPath());
		}
		return geometry;
	}
	
	private GeneralPathX createGeneralXPath(){
		GeneralPathX gp = this.gp;
		for (int i=0 ; i<innerPolygons.size() ; i++){
			GeneralPathX innerGP =((GPEPolygon)innerPolygons.get(i)).getGeneralPath();
			if (innerGP.isCCW()){
				innerGP.flip();
			}
			PathIterator it = innerGP.getPathIterator(null);
			int theType;
			double[] theData = new double[6];			
			while (!it.isDone()){
				theType = it.currentSegment(theData);
				switch (theType) {
				case PathIterator.SEG_MOVETO:
					gp.moveTo(theData[0], theData[1]);			
					break;
				case PathIterator.SEG_LINETO:
					gp.lineTo(theData[0], theData[1]);		
					break;
				case PathIterator.SEG_CLOSE:	
					break;
				}
				it.next();
			}
		}
		return gp;
	}
	
	/**
	 * @return the gp
	 */
	public GeneralPathX getGeneralPath() {
		return gp;
	}

}

