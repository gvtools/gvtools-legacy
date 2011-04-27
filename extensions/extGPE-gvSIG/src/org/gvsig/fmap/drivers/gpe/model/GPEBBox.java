package org.gvsig.fmap.drivers.gpe.model;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.gvsig.gpe.parser.ICoordinateIterator;

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
public class GPEBBox {
	private String id = null;
	private double[] x;
	private double[] y;
	private double[] z;
	private String srs;

	public GPEBBox(){
		
	}
	
	public GPEBBox(String id, ICoordinateIterator coords, String srs) {
		super();
		this.id = id;
		double[] buffer = new double[coords.getDimension()];
		this.x = new double[2];
		this.y = new double[2];
		this.z = new double[2];
		try {
			if (coords.hasNext()){
				coords.next(buffer);
				x[0] = buffer[0];
				y[0] = buffer[1];
				if (buffer.length > 2){
					z[0] = buffer[2];
				}
				if (coords.hasNext()){
					coords.next(buffer);
					x[1] = buffer[0];
					y[1] = buffer[1];
					if (buffer.length > 2){
						z[1] = buffer[2];
					}
				}					
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.srs = srs;
	}
	
	/**
	 * @return a rectangle in 2D
	 */
	public Rectangle2D getBbox2D(){
		return new Rectangle2D.Double(x[0],y[0],x[1],y[1]);
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the srs
	 */
	public String getSrs() {
		return srs;
	}
	
	/**
	 * @param srs the srs to set
	 */
	public void setSrs(String srs) {
		this.srs = srs;
	}
	
	/**
	 * @return the x
	 */
	public double[] getX() {
		return x;
	}
	
	/**
	 * @param x the x to set
	 */
	public void setX(double[] x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public double[] getY() {
		return y;
	}
	
	/**
	 * @param y the y to set
	 */
	public void setY(double[] y) {
		this.y = y;
	}
	
	/**
	 * @return the z
	 */
	public double[] getZ() {
		return z;
	}
	
	/**
	 * @param z the z to set
	 */
	public void setZ(double[] z) {
		this.z = z;
	}
}
