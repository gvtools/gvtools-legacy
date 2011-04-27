package org.gvsig.fmap.geometries.operation.println;

import java.util.HashMap;
import java.util.Map;

import org.gvsig.fmap.geometries.iso.AbstractGeometry;
import org.gvsig.fmap.geometries.iso.aggregate.MultiGeometry;
import org.gvsig.fmap.geometries.operation.GeometryOperation;
import org.gvsig.fmap.geometries.operation.GeometryOperationsRegistry;

import com.iver.cit.gvsig.fmap.core.FShape;

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
 * $Id: PrintlnOperation.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: PrintlnOperation.java,v $
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
public abstract class PrintlnDefaultOperation extends GeometryOperation{
	protected PrintlnControler controler = null;
	public static final int PRINTLN_CONTROLER = 0;
	
	/**
	 * It register all the objects that will be used to
	 * print the geometries
	 */
	public static void registerOperations(){
		GeometryOperationsRegistry.registerOperation(FShape.POINT, 
				GeometryOperation.PRINTLN_OPERATION, 
				new PrintlnPointOperation());
		GeometryOperationsRegistry.registerOperation(FShape.LINE, 
				GeometryOperation.PRINTLN_OPERATION, 
				new PrintlnCurveOperation());
		GeometryOperationsRegistry.registerOperation(FShape.POLYGON, 
				GeometryOperation.PRINTLN_OPERATION, 
				new PrintlnSurfaceOperation());
		GeometryOperationsRegistry.registerOperation(FShape.SOLID, 
				GeometryOperation.PRINTLN_OPERATION, 
				new PrintlnSolidOperation());
		GeometryOperationsRegistry.registerOperation(FShape.MULTI, 
				GeometryOperation.PRINTLN_OPERATION, 
				new PrintlnMultiGeometryOperation());		
		GeometryOperationsRegistry.registerOperation(FShape.MULTIPOINT, 
				GeometryOperation.PRINTLN_OPERATION, 
				new PrintlnMultiPointOperation());
		GeometryOperationsRegistry.registerOperation(FShape.MULTIPOLYLINE, 
				GeometryOperation.PRINTLN_OPERATION, 
				new PrintlnMultiCurveOperation());		
		GeometryOperationsRegistry.registerOperation(FShape.MULTIPOLYGON, 
				GeometryOperation.PRINTLN_OPERATION, 
				new PrintlnMultiSurfaceOperation());
		GeometryOperationsRegistry.registerOperation(FShape.MULTISOLID, 
				GeometryOperation.PRINTLN_OPERATION, 
				new PrintlnMultiSolidOperation());
		//Sets a controler for all the classes
		HashMap parameters = new HashMap();
		parameters.put(new Integer(PRINTLN_CONTROLER), new PrintlnControler());
		GeometryOperationsRegistry.setParametersPropertiesForOperation(parameters, GeometryOperation.PRINTLN_OPERATION);
	}
	
	
	/* (non-Javadoc)
	 * @see org.gvsig.geometries.operation.GeometryOperation#setParemeters(java.util.Map)
	 */
	public void setParemeters(Map parameters) {
		super.setParemeters(parameters);
		controler = (PrintlnControler) parameters.get(PRINTLN_CONTROLER);
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.geometries.operation.GeometryOperation#getOperationCode()
	 */
	public int getOperationCode() {
		return GeometryOperation.PRINTLN_OPERATION;
	}
	
	/**
	 * Print a header for each geometry:
	 */
	protected void printGeometry(AbstractGeometry geometry, String geometryName){
		if (!controler.hasChildren){
			System.out.println("********** New " + geometryName + " ***********");
		}else{
			System.out.println(controler.getTab() + "----- " + geometryName + " " + controler.childrenVisited + " -----");
		}
		System.out.println(controler.tab + "CLASS = " + geometry.getClass().toString());
		System.out.println(controler.tab + "DIMENSION = " + geometry.getCoordinateDimension());
		System.out.println(controler.tab + "IS SIMPLE = " + geometry.isSimple());		
		updateTab(geometry);
	}	
	
	/**
	 * Updates the start line tab to write the
	 * aggregate geometries 
	 * @param geometry
	 * The geometry
	 */
	private void updateTab(AbstractGeometry geometry){
		if (geometry instanceof MultiGeometry){
			controler.hasChildren = true;
			controler.childrenNumber = ((MultiGeometry)geometry).getPrimitivesNumber();
			controler.tab = "\t";
		}else{
			if (controler.hasChildren){
				controler.childrenVisited++;
				if(controler.childrenVisited == controler.childrenNumber){
					controler.setLevel0();
				}
			}
		}				
	}		
}
