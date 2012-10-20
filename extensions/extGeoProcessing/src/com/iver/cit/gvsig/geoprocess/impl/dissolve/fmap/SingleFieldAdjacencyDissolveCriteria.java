/*
 * Created on 12-may-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id: SingleFieldAdjacencyDissolveCriteria.java 13874 2007-09-19 16:12:18Z jaume $
 * $Log$
 * Revision 1.3  2007-09-19 16:06:46  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.2  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/05/24 21:11:14  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.dissolve.fmap;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Its a SingleFieldCriteria that adds adjacency condition: once a feature has
 * passed the single field value dissolve criteria (it has the same specified
 * field value) it must be adjacent to the seed feature to dissolve them.
 * 
 * @author azabala
 * 
 */

public class SingleFieldAdjacencyDissolveCriteria extends
		SingleFieldDissolveCriteria /*
									 * implements ISpatialDissolveCriteria
									 */{

	// private IGeometry firstGeometry;
	//
	// private IGeometry secondGeometry;
	//
	// private ICoordTrans ct;
	/**
	 * Cached jts geometry of the dissolve 'seed'
	 */
	// private Geometry cachedJts;
	//
	// private Geometry cachedJts2;

	public SingleFieldAdjacencyDissolveCriteria(String dissolveField,
			FLyrVect layer) throws DriverException {
		super(dissolveField, layer);
	}

	// public IGeometry getFirstGeometry(){
	// // return firstGeometry;
	// }
	//
	// public IGeometry getSecondGeometry(){
	// return secondGeometry;
	// }

	public boolean verifyIfDissolve(int featureIndex1, int featureIndex2) {
		// first the alphanumeric criteria
		if (super.verifyIfDissolve(featureIndex1, featureIndex2)) {
			// second the spatial criteria
			// FIXME: Arreglar esto
			// fetchGeometry(featureIndex1);
			// fetchGeometry2(featureIndex2);
			Geometry g1;
			Geometry g2;
			try {
				g1 = layer.getSource().getShape(featureIndex1).toJTSGeometry();
				g2 = layer.getSource().getShape(featureIndex2).toJTSGeometry();
			} catch (ReadDriverException e) {
				return false;
			}
			return g1.intersects(g2);
			// return cachedJts.intersects(cachedJts2);

		}
		return false;
	}

	/**
	 * Verify if the geometry of the seed feature has been readed, and reads it
	 * if not.
	 * 
	 * @param index
	 */
	// private void fetchGeometry(int index){
	// if(cachedJts == null){
	// try {
	// firstGeometry = layer.getSource().getShape(index);
	// if(ct != null)
	// firstGeometry.reProject(ct);
	// cachedJts = firstGeometry.toJTSGeometry();
	// } catch (DriverIOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	/**
	 * Verify if the geometry of the seed feature has been readed, and reads it
	 * if not.
	 * 
	 * @param index
	 */
	// private void fetchGeometry2(int index){
	// if(cachedJts2 == null){
	// try {
	// secondGeometry = layer.getSource().getShape(index);
	// if(ct != null)
	// secondGeometry.reProject(ct);
	// cachedJts2 = secondGeometry.toJTSGeometry();
	// } catch (DriverIOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	public void clear() {
		super.clear();
		// cachedJts = null;

	}

	// public void setFirstGeometry(IGeometry g) {
	// firstGeometry = g;
	// cachedJts = firstGeometry.toJTSGeometry();
	// }

	// public void setSecondGeometry(IGeometry g) {
	// secondGeometry = g;
	// cachedJts2 = secondGeometry.toJTSGeometry();
	// }

	// public void setCoordTrans(ICoordTrans coordTrans) {
	// this.ct = coordTrans;
	// }
	//
	//
	// public void setFirstJts(Geometry g) {
	// cachedJts = g;
	// }
	//
	// public Geometry getSecondJts(){
	// return cachedJts2;
	// }

	// public void setSecondJts(Geometry g) {
	// cachedJts2 = g;
	// }
}
