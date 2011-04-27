package org.gvsig.fmap.geometries.operation.println;

import junit.framework.TestCase;

import org.gvsig.fmap.geometries.operation.GeometryOperation;
import org.gvsig.fmap.geometries.operation.GeometryOperationException;
import org.gvsig.fmap.geometries.operation.println.PrintlnDefaultOperation;

import com.iver.cit.gvsig.fmap.core.FMultiPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
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
 * $Id: PrintlnOperationTest.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: PrintlnOperationTest.java,v $
 * Revision 1.2  2008/03/25 08:47:41  cvs
 * Visitors removed
 *
 * Revision 1.1  2008/03/12 08:46:21  cvs
 * *** empty log message ***
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (jorge.piera@iver.es)
 */
public class PrintlnOperationTest extends TestCase{

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		PrintlnDefaultOperation.registerOperations();
	}
	
	public void test1() throws GeometryOperationException{
		FPoint2D point = (FPoint2D)ShapeFactory.createPoint2D(1,1);
		point.doOperation(GeometryOperation.PRINTLN_OPERATION);		
	}
	
	public void test2() throws GeometryOperationException{
		double[] x = new double[3];
		double[] y = new double[3];
		x[0] = 1;
		y[0] = 1;
		x[1] = 2;
		y[1] = 2;
		x[2] = 3;
		y[2] = 3;
		FMultiPoint2D multiPoint = (FMultiPoint2D)ShapeFactory.createMultipoint2D(x,y);
		multiPoint.doOperation(GeometryOperation.PRINTLN_OPERATION);	
	}
	
}
