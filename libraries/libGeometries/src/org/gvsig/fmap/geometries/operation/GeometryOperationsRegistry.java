package org.gvsig.fmap.geometries.operation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;


import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
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
 * $Id: GeometryOperationsRegistry.java,v 1.1 2008/03/12 08:46:21 cvs Exp $
 * $Log: GeometryOperationsRegistry.java,v $
 * Revision 1.1  2008/03/12 08:46:21  cvs
 * *** empty log message ***
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (jorge.piera@iver.es)
 */
public class GeometryOperationsRegistry {
	private static Hashtable geometryOperations = new Hashtable(); 
	
	/**
	 * Register a new operation
	 * @param geometryType
	 * The geometry type. See the FShape class to see the types
	 * @param operationCode
	 * Operation code. See the GeometryOperation class to see the codes
	 * @param operation
	 * The operation to register.
	 */
	public static void registerOperation(int geometryType, int operationCode, GeometryOperation operation){
		GeometryOperationsSet operationsSet = null;
		Integer key = new Integer(geometryType);
		//If the operations set exists...
		if (geometryOperations.containsKey(key)){
			operationsSet = (GeometryOperationsSet)geometryOperations.get(key);
		}//If is the first time it is necessary to create a set of operations
		else{
			operationsSet = new GeometryOperationsSet(GeometryOperation.NUMBER_OF_OPERATIONS, geometryType);
			geometryOperations.put(key, operationsSet);
		}
		operationsSet.addOperation(operationCode, operation);
	}
	
	/**
	 * Gets the set of operations defined for a concrete geometry type
	 * @param geometryType
	 * The geometry type. See the FShape class to see the types
	 * @return
	 * A set of operations
	 * @throws NotRegisteredOperationSetException
	 */
	public static GeometryOperationsSet getOperationSet(int geometryType) throws NotRegisteredOperationSetException{
		Integer key = new Integer(geometryType);
		//If the operations set exists...
		if (geometryOperations.containsKey(key)){
			return (GeometryOperationsSet)geometryOperations.get(key);
		}
		throw new NotRegisteredOperationSetException(geometryType);
	}
	
	/**
	 * Gets a concrete operation for a geometry type
	 * @param geometryType
	 * The geometry type code
	 * @param operationCode
	 * The operation code
	 * @return
	 * The GeometryOpration is exists
	 * @throws NotRegisteredOperationSetException
	 */
	public static GeometryOperation getOperation(int geometryType, int operationCode) throws NotRegisteredOperationSetException{
		return getOperationSet(geometryType).getOperation(operationCode);
	}
	
	/**
	 * Sets the parameters for all the objects that implements the same
	 * operation
	 * @param parameters
	 * The parameters object
	 * @param operationCode
	 * The operation code
	 */
	public static void setParametersPropertiesForOperation(Map parameters, int operationCode){
		Enumeration enumeration = geometryOperations.keys();
		while (enumeration.hasMoreElements()){
			GeometryOperationsSet operationsSet = 
				(GeometryOperationsSet) geometryOperations.get(enumeration.nextElement());
			GeometryOperation operation = operationsSet.getOperation(operationCode);
			if (operation != null){
				operation.setParemeters(parameters);
			}
		}
	}
	
	/**
	 * Gets a list of all the classes than implements a concrete
	 * operation
	 * @param operationCode
	 * The operation code
	 * @return
	 */
	public static ArrayList getOperationsByOperationCode(int operationCode){
		ArrayList operations = new ArrayList();
		Enumeration enumeration = geometryOperations.keys();
		while (enumeration.hasMoreElements()){
			GeometryOperationsSet operationsSet = 
				(GeometryOperationsSet) geometryOperations.get(enumeration.nextElement());
			GeometryOperation operation = operationsSet.getOperation(operationCode);
			if (operation != null){
				operations.add(operation);
			}
		}
		return operations;
	}
}
