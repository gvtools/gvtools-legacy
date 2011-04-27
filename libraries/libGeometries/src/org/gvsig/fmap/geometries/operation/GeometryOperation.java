package org.gvsig.fmap.geometries.operation;

import java.util.Map;

import org.gvsig.fmap.geometries.iso.AbstractGeometry;
import org.gvsig.fmap.geometries.iso.aggregate.MultiGeometry;


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
 * $Id: GeometryOperation.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: GeometryOperation.java,v $
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
public abstract class GeometryOperation {
	//Defined operations
	public static final int NUMBER_OF_OPERATIONS = 6;
	public static final int PRINTLN_OPERATION = 0;
	public static final int DRAW2D_SYMBOL_OPERATION = 1;
	public static final int DRAW2D_INTS_SYMBOL_OPERATION = 2;
	public static final int DRAW2D_INTS_CARTOGRAPHICSUPPORT_OPERATION = 3;
	public static final int FLABEL2D_OPERATION = 4;
	public static final int DRAW3D_OPERATION = 5;
	//Private attributes
	protected Map parameters = null;
	
	/**
	 * @return the operation code
	 */
	public abstract int getOperationCode();
		
	/**
	 * It executes the operation for one geometry
	 * @param geometry
	 * The geometry
	 * @throws GeometryOperationException
	 */
	public abstract Object operate(AbstractGeometry geometry) throws GeometryOperationException;
	
	/**
	 * It does the same opertion for all the children of a 
	 * multigeometry 
	 * @param multiGeometry
	 * The multigeometry
	 * @throws GeometryOperationException
	 */
	public void operateChildren(MultiGeometry multiGeometry) throws GeometryOperationException{
		for (int i=0 ; i<multiGeometry.getPrimitivesNumber() ; i++){
			multiGeometry.getPrimitiveAt(i).doOperation(getOperationCode());
		}
	}	
	
	/**
	 * Sets a set of parameters that can be used for the
	 * operations. 
	 * @param parameters
	 * The parameters object
	 */
	public void setParemeters(Map parameters){
		this.parameters = parameters;
	}
	
}
