package com.iver.cit.gvsig.fmap.core;

import com.vividsolutions.jts.geom.Geometry;


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
 * $Id: FGeometryM.java,v 1.1 2007/10/19 10:03:45 jorpiell Exp $
 * $Log: FGeometryM.java,v $
 * Revision 1.1  2007/10/19 10:03:45  jorpiell
 * First commit
 *
 *
 */
/**
 * A Geometry with the M coordinate. It contains an FshapeM
 * that is the object that contains the geometric position.
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class FGeometryM extends FGeometry implements IGeometryM {
	private static final long serialVersionUID = -7259723180192528478L;

	public FGeometryM(FShapeM shp) {
		super(shp);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.gvsig.roads.fmap.core.IGeometryDS#getMs()
	 */
	public double[] getMs() {
		return ((FShapeM)getInternalShape()).getMs();
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometryM#setMAt(int, double)
	 */
	public void setMAt(int i, double value) {
		((FShapeM)getInternalShape()).setMAt(i, value);
	}

	public boolean isDecreasing() {
		return ((FShapeM)getInternalShape()).isDecreasing();
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometryM#revertMs()
	 */
	public void revertMs() {
		((FShapeM)getInternalShape()).revertMs();
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.FGeometry#toJTSGeometry()
	 */
	public Geometry toJTSGeometry() {
		// TODO Auto-generated method stub
		return super.toJTSGeometry();
	}

	public String toText() {
		return ((FShapeM)getInternalShape()).toText();
	}
	public IGeometry cloneGeometry() {
		return new FGeometryM((FShapeM)super.cloneGeometry().getInternalShape());
	}

}
