package com.iver.cit.gvsig.fmap.core;

import java.awt.Shape;
import java.io.Serializable;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.fmap.geometries.iso.primitive.Box;
import org.gvsig.fmap.geometries.operation.GeometryOperation;
import org.gvsig.fmap.geometries.operation.GeometryOperationException;
import org.gvsig.fmap.geometries.operation.GeometryOperationsRegistry;
import org.gvsig.fmap.geometries.operation.GeometryOperationsSet;
import org.gvsig.fmap.geometries.operation.NotRegisteredOperationSetException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
 * $Id: AbstractGeometry.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: AbstractGeometry.java,v $
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
public abstract class AbstractGeometry implements IGeometry, java.awt.Shape, Serializable, org.gvsig.fmap.geometries.iso.AbstractGeometry {
	protected String id = null;
	protected CoordinateReferenceSystem crs = null;
	protected GeometryOperationsSet operationsSet = null;
		
	public AbstractGeometry(String id, CoordinateReferenceSystem crs) {
		super();
		this.id = id;
		this.crs = crs;
		try {
			operationsSet = GeometryOperationsRegistry.getOperationSet(getGeometryType());
		} catch (NotRegisteredOperationSetException e) {
			//It is not possible to do any operation with this geometry!!!
		}
	}	

	public AbstractGeometry(CoordinateReferenceSystem crs) {
		this(null, crs);		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.geometries.iso.AbstractGeometry#getOperation(int)
	 */
	public GeometryOperation getOperation(int opeartionCode){
		return operationsSet.getOperation(opeartionCode);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.geometries.iso.AbstractGeometry#doOperation(int)
	 */
	public Object doOperation(int opeartionCode) throws GeometryOperationException{
		 return operationsSet.getOperation(opeartionCode).operate(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getInternalShape()
	 */
	public Shape getInternalShape() {
		return this;
	}	

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.geometries.iso.AbstractGeometry#getBoundary()
	 */
	public Box getBoundary() {
		return new FBox(getBounds2D());
	}		
	
	/* (non-Javadoc)
	 * @see org.gvsig.geometries.iso.AbstractGeometry#getId()
	 */
	public String getId() {
		return id;
	}

	public CoordinateReferenceSystem getSRS() {
		return crs;
	}	

	public AbstractGeometry transform(CoordinateReferenceSystem newCrs) {
		IGeometry newGeom = cloneGeometry();
		newGeom.reProject(ProjectionUtils.getCrsTransform(crs, newCrs));
		return (AbstractGeometry)newGeom;
	}	
}
