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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.FMultiPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IGeometry3D;
import com.iver.cit.gvsig.fmap.core.IGeometryM;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.drivers.shp.SHP;


/**
 * Elemento shape de tipo multipunto.
 *
 * @author Vicente Caballero Navarro
 */
public class SHPMultiPoint implements SHPShape {
	private int m_type;
	private int numpoints;
	private Point2D[] points;
	private double[] zs;

	/**
	 * Crea un nuevo SHPMultiPoint.
	 */
	public SHPMultiPoint() {
		m_type = FConstant.SHAPE_TYPE_MULTIPOINT;
	}

	/**
	 * Crea un nuevo SHPMultiPoint.
	 *
	 * @param type Tipo de multipunto.
	 *
	 * @throws ShapefileException
	 */
	public SHPMultiPoint(int type) throws ShapefileException {
		if ((type != FConstant.SHAPE_TYPE_MULTIPOINT) &&
				(type != FConstant.SHAPE_TYPE_MULTIPOINTM) &&
				(type != FConstant.SHAPE_TYPE_MULTIPOINTZ)) {
			throw new ShapefileException("No es de tipo 8, 18, o 28");
		}

		m_type = type;
	}

	/**
	 * Devuelve el tipo de multipoint en concreto.
	 *
	 * @return Tipo de multipoint.
	 */
	public int getShapeType() {
		return m_type;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.shp.SHPShape#read(MappedByteBuffer, int)
	 */
	public IGeometry read(MappedByteBuffer buffer, int type) {
		double minX = buffer.getDouble();
		double minY = buffer.getDouble();
		double maxX = buffer.getDouble();
		double maxY = buffer.getDouble();
		Rectangle2D rec = new Rectangle2D.Double(minX, minY, maxX - minX,
				maxY - maxY);
		int numpoints = buffer.getInt();
		FPoint2D[] p = new FPoint2D[numpoints];

		for (int t = 0; t < numpoints; t++) {
			double x = buffer.getDouble();
			double y = buffer.getDouble();
			p[t] = new FPoint2D(x, y);
		}

		/*   if (m_type == FConstant.SHAPE_TYPE_MULTIPOINTZ) {
		   buffer.position(buffer.position() + (2 * 8));
		   for (int t = 0; t < numpoints; t++) {
		       p[t].z = buffer.getDouble(); //z
		   }
		   }
		 */
		return (IGeometry) new FMultiPoint2D(p);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.shp.SHPShape#write(ByteBuffer, IGeometry)
	 */
	public void write(ByteBuffer buffer, IGeometry geometry) {
		// FMultiPoint2D mp = (FMultiPoint2D) geometry.getShape();
		int p = buffer.position();

		Rectangle2D box = geometry.getBounds2D();
		buffer.putDouble(box.getMinX());
		buffer.putDouble(box.getMinY());
		buffer.putDouble(box.getMaxX());
		buffer.putDouble(box.getMaxY());
		///obtainsPoints(geometry.getGeneralPathXIterator());
		buffer.putInt(numpoints);

		for (int t = 0, tt = numpoints; t < tt; t++) {
			Point2D point = points[t];
			buffer.putDouble(point.getX());
			buffer.putDouble(point.getY());
		}

		  if (m_type == FConstant.SHAPE_TYPE_MULTIPOINTZ) {
		   double[] zExtreame = SHP.getZMinMax(zs);
		   if (Double.isNaN(zExtreame[0])) {
		       buffer.putDouble(0.0);
		       buffer.putDouble(0.0);
		   } else {
		       buffer.putDouble(zExtreame[0]);
		       buffer.putDouble(zExtreame[1]);
		   }
		   for (int t = 0; t < numpoints; t++) {
		       double z = zs[t];
		       if (Double.isNaN(z)) {
		           buffer.putDouble(0.0);
		       } else {
		           buffer.putDouble(z);
		       }
		   }
		   }
		  if (m_type == FConstant.SHAPE_TYPE_MULTIPOINTM){
		       buffer.putDouble(-10E40);
		       buffer.putDouble(-10E40);
		       double[] ms = ((IGeometryM)geometry).getMs();
		       for (int t = 0; t < numpoints; t++) {		    	   
		    	   buffer.putDouble(ms[t]);
		       }
		   }

	}

	/**
	 * @see com.iver.cit.gvsig.fmap.shp.SHPShape#getLength(com.iver.cit.gvsig.core.BasicShape.FGeometry)
	 */
	public int getLength(IGeometry fgeometry) {
		//FMultiPoint2D mp = (FMultiPoint2D) fgeometry.getShape();
		///obtainsPoints(fgeometry.getGeneralPathXIterator());

		int length;

		if (m_type == FConstant.SHAPE_TYPE_MULTIPOINT) {
			// two doubles per coord (16 * numgeoms) + 40 for header
			length = (numpoints * 16) + 40;
		} else if (m_type == FConstant.SHAPE_TYPE_MULTIPOINTM) {
			// add the additional MMin, MMax for 16, then 8 per measure
			length = (numpoints * 16) + 40 + 16 + (8 * numpoints);
		} else if (m_type == FConstant.SHAPE_TYPE_MULTIPOINTZ) {
			// add the additional ZMin,ZMax, plus 8 per Z
			length = (numpoints * 16) + 40 + 16 + (8 * numpoints);
		} else {
			throw new IllegalStateException("Expected ShapeType of Arc, got " +
				m_type);
		}

		return length;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.shp.write.SHPShape#obtainsPoints(com.iver.cit.gvsig.fmap.core.GeneralPathXIterator)
	 */
	public void obtainsPoints(IGeometry g) {
		if (FConstant.SHAPE_TYPE_MULTIPOINTZ == m_type){
			zs=((IGeometry3D)g).getZs();
		}
		PathIterator theIterator = g.getPathIterator(null); //polyLine.getPathIterator(null, flatness);
		double[] theData = new double[6];
		ArrayList ps=new ArrayList();
		while (!theIterator.isDone()) {
			//while not done
			int theType = theIterator.currentSegment(theData);

			ps.add(new Point2D.Double(theData[0], theData[1]));
			theIterator.next();
		} //end while loop
		points=(Point2D[])ps.toArray(new Point2D.Double[0]);
		numpoints=points.length;
	}
//	public void setFlatness(double flatness) {
//		//this.flatness=flatness;
//	}
}
