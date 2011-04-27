package org.gvsig.fmap.drivers.gpe.model;

import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.FPolyline3D;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.Handler;
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
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class GPECurve extends GPEGeometry{
	private ArrayList segments = null;
	
	public GPECurve(String id, IGeometry geometry, String srs) {
		super(id, geometry, srs);		
		segments = new ArrayList();
	}
	
	public GPECurve(String id, String srs) {
		this(id, null, srs);		
	}
	
	/**
	 * Adds a segment 
	 * @param segment
	 * The segment to add
	 */
	public void addSegment(GPEGeometry segment){
		segments.add(segment);
	}
	
	/**
	 * Gets a segment
	 * @param i
	 * The segment position
	 * @return
	 * A Segment
	 */
	public GPEGeometry getSegmentAt(int i){
		return (GPEGeometry)segments.get(i);
	}
	
	/**
	 * @return the number of seg
	 */
	public int getSegmentsSize(){
		return segments.size();
	}
	
	/**
	 * @return the geometry
	 */
	public IGeometry getIGeometry() {
		IGeometry geom = super.getIGeometry();
		if (geom != null){
			return geom;
		}
		GeneralPathX gpx = new GeneralPathX();
		int zLength = 0;
		boolean startPoint = true;
		for (int i=0 ; i<segments.size() ; i++){
			IGeometry segment = ((GPEGeometry)segments.get(i)).getIGeometry();
			Handler[] handlers = ((FPolyline3D)segment.getInternalShape()).getSelectHandlers();
			zLength = zLength + handlers.length;
			for (int j=0 ; j<handlers.length ; j++){
				if (startPoint){
					gpx.moveTo(handlers[j].getPoint().getX(),
							handlers[j].getPoint().getY());
					startPoint = false;
				}else{
					gpx.lineTo(handlers[j].getPoint().getX(),
							handlers[j].getPoint().getY());
				}
			}
		}
		//Copy the z coordinate
		double[] z = new double[zLength];
		int k = 0;
		for (int i=0 ; i<segments.size() ; i++){
			IGeometry segment = ((GPEGeometry)segments.get(i)).getIGeometry();
			double[] zAux = ((FPolyline3D)segment.getInternalShape()).getZs();
			System.arraycopy(zAux, 0, z, k, zAux.length);
			k = k + zAux.length;
		}
		geometry = ShapeFactory.createPolyline3D(gpx, z);
		return geometry;
	}	

}
