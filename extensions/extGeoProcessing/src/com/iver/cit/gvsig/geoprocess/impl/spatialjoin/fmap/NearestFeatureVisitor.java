/*
 * Created on 01-mar-2006
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
* $Id: NearestFeatureVisitor.java 10626 2007-03-06 16:55:54Z caballero $
* $Log$
* Revision 1.2  2007-03-06 16:47:58  caballero
* Exceptions
*
* Revision 1.1  2006/06/20 18:20:45  azabala
* first version in cvs
*
* Revision 1.2  2006/06/02 18:21:28  azabala
* *** empty log message ***
*
* Revision 1.1  2006/05/24 21:09:47  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.2  2006/03/07 21:01:33  azabala
* *** empty log message ***
*
* Revision 1.1  2006/03/06 19:48:39  azabala
* *** empty log message ***
*
* Revision 1.1  2006/03/05 19:59:47  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.spatialjoin.fmap;


import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.vividsolutions.jts.geom.Geometry;
/**
 * This Visitor looks for the nearest feature of a FLayer B
 *  to a specified feature of a FLayer A.
 * @author azabala
 *
 */
public class NearestFeatureVisitor implements FeatureVisitor {
	
	private Geometry queryGeometry;
	private int solutionIndex = -1;
	private double shortestDist = Double.MAX_VALUE;
	
	public NearestFeatureVisitor(Geometry geometry){
		this.queryGeometry = geometry;
	}
	
	public NearestFeatureVisitor(){	
	}
	
	public void visit(IGeometry g, int index) throws VisitorException, ProcessVisitorException {
		if(g == null)
			return;
		double dist = queryGeometry.distance(g.toJTSGeometry());
		if(dist < shortestDist){
			shortestDist = dist;
			solutionIndex = index;
		}//if
	}
	
	public int getNearestFeatureIndex(){
		return solutionIndex;
	}
	
	public double getShortestDist(){
		return shortestDist;
	}
	
	public boolean hasFoundShortest(){
		return solutionIndex != -1;
	}
	
	public void stop(FLayer layer) throws VisitorException {
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		if (layer instanceof VectorialData) {
			return true;
		}
		return false;
	}
	
	public String getProcessDescription() {
		return "Looking nearest geometry for a spatial join";
	}


	public void setQueryGeometry(Geometry queryGeometry) {
		this.queryGeometry = queryGeometry;
	}

}

