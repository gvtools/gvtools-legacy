package org.gvsig.fmap.geometries;

import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.geometries.operation.GeometryOperation;

import com.iver.cit.gvsig.fmap.core.FGeometry;
import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;

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
/* CVS MESSAGES:
 *
 * $Id: MultiGeometry2DTest.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: MultiGeometry2DTest.java,v $
 * Revision 1.2  2008/03/25 08:47:41  cvs
 * Visitors removed
 *
 * Revision 1.1  2008/03/12 08:46:20  cvs
 * *** empty log message ***
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (jorge.piera@iver.es)
 */
public class MultiGeometry2DTest extends AbstractGeometriesTest{
		
	public void testMultiPointPoint1() throws BaseException{
		FGeometry[] geometries = new FGeometry[3];
		//Add a point
		geometries[0] = (FPoint2D)ShapeFactory.createPoint2D(1,1);
		
		//Add a line
		GeneralPathX gp = new GeneralPathX();
		gp.moveTo(1, 1);
		gp.lineTo(2, 2);
		gp.lineTo(3, 3);
		geometries[1] = (FPolyline2D)ShapeFactory.createPolyline2D(gp);
		
		//Add a polygon
		GeneralPathX gp2 = new GeneralPathX();
		gp2.moveTo(4, 4);
		gp2.lineTo(5, 5);
		gp2.lineTo(6, 6);
		geometries[2] = (FPolygon2D)ShapeFactory.createPolygon2D(gp2);
		
		FGeometryCollection multiGeometry = (FGeometryCollection)ShapeFactory.createMultiGeometry(geometries);
		multiGeometry.doOperation(GeometryOperation.PRINTLN_OPERATION);	
		assertEquals(multiGeometry.getCoordinateDimension(), 2);
		assertEquals(multiGeometry.isSimple(), false);
		assertEquals(multiGeometry.getPrimitivesNumber(), 3);
		
		//Asserts for the first geometry
		FPoint2D point1 = (FPoint2D)multiGeometry.getPrimitiveAt(0);
		assertEquals(point1.getX(),1.0);
		assertEquals(point1.getY(),1.0);
		
		//Asserts for the last geometry
		FPolygon2D line2 = (FPolygon2D)multiGeometry.getPrimitiveAt(multiGeometry.getPrimitivesNumber()-1);
		Handler[] handlers2 = line2.getSelectHandlers();
		assertEquals(handlers2[0].getPoint().getX(),4.0);
		assertEquals(handlers2[0].getPoint().getY(),4.0);
		assertEquals(handlers2[handlers2.length-1].getPoint().getX(),6.0);
		assertEquals(handlers2[handlers2.length-1].getPoint().getY(),6.0);
	}
}
	