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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.drivers.shp.SHP;
import com.iver.cit.gvsig.fmap.drivers.shp.ShapeFileHeader;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class SHPFileWrite {
	private SHPShape m_shape = null;
	private ByteBuffer m_bb = null;
	private ByteBuffer m_indexBuffer = null;
	private int m_pos = 0;
	private int m_offset;
	private int m_type;
	private int m_cnt;
	private FileChannel shpChannel;
	private FileChannel shxChannel;

	// private double flatness;

	/**
	 * Crea un nuevo SHPFileWrite.
	 * 
	 * @param shpChannel
	 *            DOCUMENT ME!
	 * @param shxChannel
	 *            DOCUMENT ME!
	 */
	public SHPFileWrite(FileChannel shpChannel, FileChannel shxChannel) {
		this.shpChannel = shpChannel;
		this.shxChannel = shxChannel;
	}

	/**
	 * Make sure our buffer is of size.
	 * 
	 * @param size
	 *            DOCUMENT ME!
	 */
	private void checkShapeBuffer(int size) {
		if (m_bb.capacity() < size) {
			m_bb = ByteBuffer.allocateDirect(size);
		}
	}

	/**
	 * Drain internal buffers into underlying channels.
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	private void drain() throws IOException {
		m_bb.flip();
		m_indexBuffer.flip();

		while (m_bb.remaining() > 0)
			shpChannel.write(m_bb);

		while (m_indexBuffer.remaining() > 0)
			shxChannel.write(m_indexBuffer);

		m_bb.flip().limit(m_bb.capacity());
		m_indexBuffer.flip().limit(m_indexBuffer.capacity());
	}

	/**
	 * DOCUMENT ME!
	 */
	private void allocateBuffers() {
		m_bb = ByteBuffer.allocateDirect(16 * 1024);
		m_indexBuffer = ByteBuffer.allocateDirect(100);
	}

	/**
	 * Close the underlying Channels.
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void close() throws IOException {
		shpChannel.close();
		shxChannel.close();
		shpChannel = null;
		shxChannel = null;
		m_shape = null;

		if (m_indexBuffer instanceof ByteBuffer) {
			if (m_indexBuffer != null) {
				// /NIOUtilities.clean(m_indexBuffer);
			}
		}

		if (m_indexBuffer instanceof ByteBuffer) {
			if (m_indexBuffer != null) {
				// /NIOUtilities.clean(m_bb);
			}
		}

		m_indexBuffer = null;
		m_bb = null;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param geometries
	 *            DOCUMENT ME!
	 * @param type
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 * @throws ShapefileException
	 *             DOCUMENT ME!
	 */
	public void write(IGeometry[] geometries, int type) throws IOException,
			ShapefileException {
		m_shape = SHP.create(type);
		// m_shape.setFlatness(flatness);
		writeHeaders(geometries, type);

		m_pos = m_bb.position();

		for (int i = 0, ii = geometries.length; i < ii; i++) {
			writeGeometry(geometries[i]);
		}

		close();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param geometries
	 *            DOCUMENT ME!
	 * @param type
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	private void writeHeaders(IGeometry[] geometries, int type)
			throws IOException {
		int fileLength = 100;
		Rectangle2D extent = null;

		for (int i = geometries.length - 1; i >= 0; i--) {
			IGeometry fgeometry = geometries[i];
			m_shape.obtainsPoints(fgeometry);
			int size = m_shape.getLength(fgeometry) + 8;
			fileLength += size;

			if (extent == null) {
				extent = new Rectangle2D.Double(fgeometry.getBounds2D()
						.getMinX(), fgeometry.getBounds2D().getMinY(),
						fgeometry.getBounds2D().getWidth(), fgeometry
								.getBounds2D().getHeight());
			} else {
				extent.add(fgeometry.getBounds2D());
			}
		}

		writeHeaders(extent, type, geometries.length, fileLength);
	}

	/**
	 * Writes shape header (100 bytes)
	 * 
	 * @param bounds
	 *            DOCUMENT ME!
	 * @param type
	 *            DOCUMENT ME!
	 * @param numberOfGeometries
	 *            DOCUMENT ME!
	 * @param fileLength
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void writeHeaders(Rectangle2D bounds, int type,
			int numberOfGeometries, int fileLength) throws IOException {
		/*
		 * try { handler = type.getShapeHandler(); } catch (ShapefileException
		 * se) { throw new RuntimeException("unexpected Exception",se); }
		 */
		if (m_bb == null) {
			allocateBuffers();
		}
		// Posicionamos al principio.
		m_bb.position(0);
		m_indexBuffer.position(0);

		ShapeFileHeader header = new ShapeFileHeader();

		header.write(m_bb, type, numberOfGeometries, fileLength / 2,
				bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(),
				bounds.getMaxY(), 0, 0, 0, 0);

		header.write(m_indexBuffer, type, numberOfGeometries,
				50 + (4 * numberOfGeometries), bounds.getMinX(),
				bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), 0, 0, 0,
				0);

		m_offset = 50;
		m_type = type;
		m_cnt = 0;

		shpChannel.position(0);
		shxChannel.position(0);
		drain();
	}

	public synchronized int writeIGeometry(IGeometry g) throws IOException,
			ShapefileException {
		int shapeType = getShapeType(g.getGeometryType());
		m_shape = SHP.create(shapeType);
		// m_shape.setFlatness(flatness);
		return writeGeometry(g);
	}

	/**
	 * Writes a single Geometry.
	 * 
	 * @param g
	 * @return the position of buffer (after the last geometry, it will allow
	 *         you to write the file size in the header.
	 * @throws IOException
	 */
	public synchronized int writeGeometry(IGeometry g) throws IOException {
		if (m_bb == null) {
			allocateBuffers();
			m_offset = 50;
			m_cnt = 0;

			shpChannel.position(0);
			shxChannel.position(0);

			// throw new IOException("Must write headers first");
		}

		m_pos = m_bb.position();
		m_shape.obtainsPoints(g);
		int length = m_shape.getLength(g);

		// must allocate enough for shape + header (2 ints)
		checkShapeBuffer(length + 8);

		length /= 2;

		m_bb.order(ByteOrder.BIG_ENDIAN);
		m_bb.putInt(++m_cnt);
		m_bb.putInt(length);
		m_bb.order(ByteOrder.LITTLE_ENDIAN);
		m_bb.putInt(m_type);
		m_shape.write(m_bb, g);

		// /assert (length * 2 == (m_bb.position() - m_pos) - 8);
		m_pos = m_bb.position();

		// write to the shx
		m_indexBuffer.putInt(m_offset);
		m_indexBuffer.putInt(length);
		m_offset += (length + 4);
		drain();

		// /assert(m_bb.position() == 0);
		return m_pos; // Devolvemos hasta donde hemos escrito
	}

	/**
	 * Returns a shapeType compatible with shapeFile constants from a gvSIG's
	 * IGeometry type
	 * 
	 * @param geometryType
	 * @return a shapeType compatible with shapeFile constants from a gvSIG's
	 *         IGeometry type
	 */
	public int getShapeType(int geometryType) {
		int type = geometryType;
		if (geometryType >= FShape.M) {
			type = geometryType - FShape.M;
			switch (type) {
			case (FShape.POINT):
				return FConstant.SHAPE_TYPE_POINTM;

			case (FShape.LINE):
				return FConstant.SHAPE_TYPE_POLYLINEM;

			case FShape.POLYGON:
				return FConstant.SHAPE_TYPE_POLYGONM;

			case FShape.MULTIPOINT:
				return FConstant.SHAPE_TYPE_MULTIPOINTM; // TODO falta aclarar
															// cosas aquï¿½.
			}
		} else if (geometryType >= FShape.Z) {
			type = geometryType - FShape.Z;
			switch (geometryType - FShape.Z) {
			case (FShape.POINT):
				return FConstant.SHAPE_TYPE_POINTZ;

			case (FShape.LINE):
				return FConstant.SHAPE_TYPE_POLYLINEZ;

			case FShape.POLYGON:
				return FConstant.SHAPE_TYPE_POLYGONZ;

			case FShape.MULTIPOINT:
				return FConstant.SHAPE_TYPE_MULTIPOINTZ; // TODO falta aclarar
															// cosas aquí.
			}

		} else {
			switch (geometryType) {
			case FShape.POINT:
				return FConstant.SHAPE_TYPE_POINT;

			case FShape.LINE:
			case FShape.ELLIPSE:
			case FShape.CIRCLE:
			case FShape.ARC:
				return FConstant.SHAPE_TYPE_POLYLINE;

			case FShape.POLYGON:
				return FConstant.SHAPE_TYPE_POLYGON;

			case FShape.MULTIPOINT:
				return FConstant.SHAPE_TYPE_MULTIPOINT; // TODO falta aclarar
														// cosas aquí.
			}
		}
		return FConstant.SHAPE_TYPE_NULL;
	}

	// public void setFlatness(double flatness) {
	// this.flatness=flatness;
	// }

}
