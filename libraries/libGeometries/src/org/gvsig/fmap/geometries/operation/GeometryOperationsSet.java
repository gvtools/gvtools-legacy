package org.gvsig.fmap.geometries.operation;



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
 * $Id: GeometryOperationsSet.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: GeometryOperationsSet.java,v $
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
public class GeometryOperationsSet {
	private GeometryOperation[] operations = null;
	private int geometryType = -1;	
	
	GeometryOperationsSet(int size, int geometryType){
		operations = new GeometryOperation[size];
		this.geometryType = geometryType;
	}
	
	void addOperation(int index, GeometryOperation operation){
		if (index > operations.length){
			GeometryOperation[] operationsAux = new GeometryOperation[index];
			System.arraycopy(operationsAux,0,operations,0,operations.length);
			operationsAux[index] = operation;
			System.arraycopy(operations,0,operationsAux,0,operationsAux.length);
		}
		operations[index] = operation; 		
	}
	
	public GeometryOperation getOperation(int index){
		return operations[index];
	}
}
