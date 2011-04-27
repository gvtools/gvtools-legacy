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
package com.iver.cit.gvsig.fmap.drivers.shp.write;

import java.awt.geom.PathIterator;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint2DM;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IGeometry3D;
import com.iver.cit.gvsig.fmap.core.IGeometryM;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class SHPPoint implements SHPShape {
	private int m_type;
	private FPoint2D point;
	private double z;

	/**
	 * Crea un nuevo SHPPoint.
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @throws ShapefileException DOCUMENT ME!
	 */
	public SHPPoint(int type) throws ShapefileException {
		if ((type != FConstant.SHAPE_TYPE_POINT) &&
				(type != FConstant.SHAPE_TYPE_POINTM) &&
				(type != FConstant.SHAPE_TYPE_POINTZ)) { // 2d, 2d+m, 3d+m
			throw new ShapefileException("No es un punto 1,11 ni 21");
		}

		m_type = type;
	}

	/**
	 * Crea un nuevo SHPPoint.
	 */
	public SHPPoint() {
		m_type = FConstant.SHAPE_TYPE_POINT; //2d
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.shp.SHPShape#getShapeType()
	 */
	public int getShapeType() {
		return m_type;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.shp.SHPShape#read(MappedByteBuffer, int)
	 */
	public IGeometry read(MappedByteBuffer buffer, int type) {
		double x = buffer.getDouble();
		double y = buffer.getDouble();
		double z = Double.NaN;

		if (m_type == FConstant.SHAPE_TYPE_POINTM) {
			buffer.getDouble();
		}

		if (m_type == FConstant.SHAPE_TYPE_POINTZ) {
			z = buffer.getDouble();
		}

		return (IGeometry) new FPoint2D(x, y);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.shp.SHPShape#write(ByteBuffer, IGeometry)
	 */
	public void write(ByteBuffer buffer, IGeometry geometry) {
		//FPoint2D p2d = ((FPoint2D) geometry.getShape());
		///obtainsPoints(geometry.getGeneralPathXIterator());
		buffer.putDouble(point.getX());
		buffer.putDouble(point.getY());

		if (m_type == FConstant.SHAPE_TYPE_POINTZ) {
			if (Double.isNaN(z)) { // nan means not defined
				buffer.putDouble(0.0);
			} else {
				buffer.putDouble(z);
			}
		}
		if (m_type == FConstant.SHAPE_TYPE_POINTM) {
			buffer.putDouble(((FPoint2DM)point).getM());
		}
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.shp.SHPShape#getLength(int)
	 */
	public int getLength(IGeometry fgeometry) {
		int length;

		if (m_type == FConstant.SHAPE_TYPE_POINT) {
			length = 20;
		} else if (m_type == FConstant.SHAPE_TYPE_POINTM ||
				m_type == FConstant.SHAPE_TYPE_POINTZ) {
			length = 28;
		} else {
			throw new IllegalStateException("Expected ShapeType of Point, got" +
					m_type);
		}

		return length;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.shp.write.SHPShape#obtainsPoints(com.iver.cit.gvsig.fmap.core.GeneralPathXIterator)
	 */
	public void obtainsPoints(IGeometry g) {
		if (FConstant.SHAPE_TYPE_POINTZ == m_type){
			z=((IGeometry3D)g).getZs()[0];
		}else if (FConstant.SHAPE_TYPE_POINTM == m_type){
			z=((IGeometryM)g).getMs()[0];
		}
		PathIterator theIterator = g.getPathIterator(null); //polyLine.getPathIterator(null, flatness);
		double[] theData = new double[6];

		while (!theIterator.isDone()) {
			//while not done
			int theType = theIterator.currentSegment(theData);

			point = new FPoint2D(theData[0], theData[1]);

			theIterator.next();
		} //end while loop
	}
	//	public void setFlatness(double flatness) {
	//	//	this.flatness=flatness;
	//	}
}
