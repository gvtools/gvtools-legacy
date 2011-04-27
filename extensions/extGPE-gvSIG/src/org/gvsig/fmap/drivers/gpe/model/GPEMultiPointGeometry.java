package org.gvsig.fmap.drivers.gpe.model;

import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
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
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera Llodrá (jorge.piera@iver.es)
 */
public class GPEMultiPointGeometry extends GPEMultiGeometry{

	public GPEMultiPointGeometry(String id, String srs) {
		super(id, srs);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.drivers.gpe.model.GPEMultiGeometry#getIGeometry()
	 */
	public IGeometry getIGeometry() {
		if (geometry == null){
			double[] x = new double[getGeometriesSize()];
			double[] y = new double[getGeometriesSize()];
			for (int i=0 ; i<getGeometriesSize() ; i++){
				GPEGeometry geom = (GPEGeometry)getGeometryAt(i);
				FPoint2D point = (FPoint2D)geom.getIGeometry().getInternalShape();
				x[i] = point.getX();
				y[i] = point.getY();				
			}
			geometry =  ShapeFactory.createMultipoint2D(x, y);
		}
		return geometry;
	}
}
