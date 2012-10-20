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

import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IGeometryM;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.drivers.shp.SHP;

/**
 * Elemento shape de tipo Polígono.
 * 
 * @author Vicente Caballero Navarro
 */
public class SHPPolygon extends SHPMultiLine {

	/**
	 * Crea un nuevo SHPPolygon.
	 */
	public SHPPolygon() {
		m_type = FConstant.SHAPE_TYPE_POLYGON;
	}

	/**
	 * Crea un nuevo SHPPolygon.
	 * 
	 * @param type
	 *            Tipo de shape.
	 * 
	 * @throws ShapefileException
	 */
	public SHPPolygon(int type) throws ShapefileException {
		if ((type != FConstant.SHAPE_TYPE_POLYGON)
				&& (type != FConstant.SHAPE_TYPE_POLYGONM)
				&& (type != FConstant.SHAPE_TYPE_POLYGONZ)) {
			throw new ShapefileException("No es de tipo 5, 15, o 25");
		}

		m_type = type;
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
	public synchronized IGeometry read(MappedByteBuffer buffer, int type) {
		double minX = buffer.getDouble();
		double minY = buffer.getDouble();
		double maxX = buffer.getDouble();
		double maxY = buffer.getDouble();
		Rectangle2D rec = new Rectangle2D.Double(minX, minY, maxX - minX, maxY
				- maxY);
		int numParts = buffer.getInt();
		int numPoints = buffer.getInt();

		int[] partOffsets = new int[numParts];

		for (int i = 0; i < numParts; i++) {
			partOffsets[i] = buffer.getInt();
		}

		FPoint2D[] points = readPoints(buffer, numPoints);

		/*
		 * if (m_type == FConstant.SHAPE_TYPE_POLYGONZ) { //z
		 * buffer.position(buffer.position() + (2 * 8)); for (int t = 0; t <
		 * numPoints; t++) { points[t].z = buffer.getDouble(); } }
		 */
		int offset = 0;
		int start;
		int finish;
		int length;

		for (int part = 0; part < numParts; part++) {
			start = partOffsets[part];

			if (part == (numParts - 1)) {
				finish = numPoints;
			} else {
				finish = partOffsets[part + 1];
			}

			length = finish - start;

			FPoint2D[] pointsPart = new FPoint2D[length];

			for (int i = 0; i < length; i++) {
				pointsPart[i] = points[offset++];
			}
		}

		return (IGeometry) new FPolygon2D(getGeneralPathX(points, partOffsets));
	}

	/**
	 * Lee los puntos del buffer.
	 * 
	 * @param buffer
	 * @param numPoints
	 *            Número de puntos.
	 * 
	 * @return Vector de Puntos.
	 */
	private synchronized FPoint2D[] readPoints(final MappedByteBuffer buffer,
			final int numPoints) {
		FPoint2D[] points = new FPoint2D[numPoints];

		for (int t = 0; t < numPoints; t++) {
			points[t] = new FPoint2D(buffer.getDouble(), buffer.getDouble());
		}

		return points;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.shp.SHPShape#write(ByteBuffer, IGeometry)
	 */
	public synchronized void write(ByteBuffer buffer, IGeometry geometry) {
		// FPolygon2D polyLine;
		// polyLine = (FPolygon2D) geometry.getShape();
		Rectangle2D rec = geometry.getBounds2D();
		buffer.putDouble(rec.getMinX());
		buffer.putDouble(rec.getMinY());
		buffer.putDouble(rec.getMaxX());
		buffer.putDouble(rec.getMaxY());

		// ////
		// /obtainsPoints(geometry.getGeneralPathXIterator());

		// int[] parts=polyLine.getParts();
		// FPoint2D[] points=polyLine.getPoints();
		int nparts = parts.length;
		int npoints = points.length;

		// ////
		// /int npoints = polyLine.getNumPoints();
		// /int nparts = polyLine.getNumParts();
		buffer.putInt(nparts);
		buffer.putInt(npoints);

		int count = 0;

		for (int t = 0; t < nparts; t++) {
			// /buffer.putInt(polyLine.getPart(t));
			buffer.putInt(parts[t]);
		}

		// /FPoint[] points = polyLine.getPoints();
		for (int t = 0; t < points.length; t++) {
			// /buffer.putDouble(points[t].x);
			// /buffer.putDouble(points[t].y);
			buffer.putDouble(points[t].getX());
			buffer.putDouble(points[t].getY());
		}

		if (m_type == FConstant.SHAPE_TYPE_POLYGONZ) {
			double[] zExtreame = SHP.getZMinMax(zs);
			if (Double.isNaN(zExtreame[0])) {
				buffer.putDouble(0.0);
				buffer.putDouble(0.0);
			} else {
				buffer.putDouble(zExtreame[0]);
				buffer.putDouble(zExtreame[1]);
			}
			for (int t = 0; t < npoints; t++) {
				double z = zs[t];
				if (Double.isNaN(z)) {
					buffer.putDouble(0.0);
				} else {
					buffer.putDouble(z);
				}
			}
		}

		if (m_type == FConstant.SHAPE_TYPE_POLYGONM) {
			buffer.putDouble(-10E40);
			buffer.putDouble(-10E40);
			double[] ms = ((IGeometryM) geometry).getMs();
			for (int t = 0; t < npoints; t++) {
				if (npoints >= ms.length)
					buffer.putDouble(ms[0]); // Era un polígono cerrado que no
												// terminaba en el primer punto
				else
					buffer.putDouble(ms[t]);
			}
		}
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.shp.SHPShape#getLength(com.iver.cit.gvsig.core.BasicShape.FGeometry)
	 */
	public synchronized int getLength(IGeometry fgeometry) {
		// FPolygon2D multi;
		// multi = (FPolygon2D) fgeometry.getShape();
		// /int nrings = 0;
		// /obtainsPoints(fgeometry.getGeneralPathXIterator());

		// int[] parts=multi.getParts();
		// FPoint2D[] points;
		// ///////
		// points = multi.getPoints();
		int npoints = points.length;

		// /////////
		// /nrings = multi.getNumParts();
		// /int npoints = multi.getNumPoints();
		int length;

		if (m_type == FConstant.SHAPE_TYPE_POLYGONZ) {
			length = 44 + (4 * parts.length) + (16 * npoints) + (8 * npoints)
					+ 16;
		} else if (m_type == FConstant.SHAPE_TYPE_POLYGONM) {
			length = 44 + (4 * parts.length) + (16 * npoints) + (8 * npoints)
					+ 16;
		} else if (m_type == FConstant.SHAPE_TYPE_POLYGON) {
			length = 44 + (4 * parts.length) + (16 * npoints);
		} else {
			throw new IllegalStateException(
					"Expected ShapeType of Polygon, got " + m_type);
		}

		return length;
	}
}
