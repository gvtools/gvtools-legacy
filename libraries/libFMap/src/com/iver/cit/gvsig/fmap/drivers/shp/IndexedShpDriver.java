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
package com.iver.cit.gvsig.fmap.drivers.shp;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.DriverUtilities;
import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.FileNotFoundDriverException;
import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.ShapeMFactory;
import com.iver.cit.gvsig.fmap.core.ShapeZMFactory;
import com.iver.cit.gvsig.fmap.drivers.BoundedShapes;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.ExternalData;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbfEncodings;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.utiles.bigfile.BigByteBuffer2;

/**
 * Driver del formato SHP. Usa ByteBuffer2, que no consume memoria (bueno,
 * 8KBytes... !) y no le importa que le pidan entidades desordenadas del
 * fichero, lo cual es indispensable para trabajar con índices espaciales.
 * Además, el tiempo de espera para abrir un fichero y empezar a pintar es
 * prácticamente nulo, tanto desde disco duro como por red. La única pega es que
 * en ficheros grandes puede ser unos milisegundos un poco más lento que el
 * otro, pero muy poco.
 * 
 * @author Francisco José Peñarrubia
 */
public class IndexedShpDriver implements VectorialFileDriver, BoundedShapes,
		ExternalData, IWriteable, ISpatialWriter {
	private static Logger logger = Logger.getLogger(IndexedShpDriver.class
			.getName());
	private static String tempDirectoryPath = System
			.getProperty("java.io.tmpdir");
	private File fileShp;
	private File fTemp;
	private BigByteBuffer2 bb;
	private FileChannel channel;
	private FileInputStream fin;
	private int type;
	// private long[] m_posShapes;
	private int numReg;
	private Rectangle2D extent;

	// private File shxFile;
	private BigByteBuffer2 bbShx;
	private FileChannel channelShx;
	private FileInputStream finShx;

	private ShpWriter shpWriter = new ShpWriter();
	private Charset charset;

	/**
	 * Cierra el fichero.
	 * 
	 * @throws IOException
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#close()
	 */
	public void close() throws CloseDriverException {
		CloseDriverException ret = null;

		try {
			channel.close();
			channelShx.close();
		} catch (IOException e) {
			ret = new CloseDriverException(getName(), e);
		} finally {
			try {
				fin.close();
			} catch (IOException e1) {
				ret = new CloseDriverException(getName(), e1);
			}
		}

		if (ret != null)
			throw ret;
		bb = null;
		bbShx = null;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#open(java.io.File)
	 */
	public void open(File f) throws OpenDriverException {
		fileShp = f;

		try {
			fin = new FileInputStream(f);

			// Open the file and then get a channel from the stream
			channel = fin.getChannel();

			// long size = channel.size();

			// Get the file's size and then map it into memory
			// bb = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
			bb = new BigByteBuffer2(channel, FileChannel.MapMode.READ_ONLY);

			finShx = new FileInputStream(SHP.getShxFile(f));

			// Open the file and then get a channel from the stream
			channelShx = finShx.getChannel();

			long sizeShx = channelShx.size();

			// Get the file's size and then map it into memory
			// bb = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
			// bbShx = channelShx.map(FileChannel.MapMode.READ_ONLY, 0,
			// sizeShx);
			bbShx = new BigByteBuffer2(channelShx,
					FileChannel.MapMode.READ_ONLY);
			bbShx.order(ByteOrder.BIG_ENDIAN);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundDriverException(getName(), e,
					f.getAbsolutePath());
		} catch (IOException e) {
			throw new OpenDriverException(getName(), e);
		}
	}

	/**
	 * Devuelve la geometria a partir de un índice.
	 * 
	 * @param index
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public synchronized IGeometry getShape(int index)
			throws ReadDriverException {
		Point2D.Double p = new Point2D.Double();
		int numParts;
		int numPoints;
		int i;
		int j;
		/*
		 * int numReg; int numeroPuntos; int hasta; int desde;
		 */
		int shapeType;

		// Rectangle2D.Double BoundingBox;
		// if (m_posShapes[index] == 0)

		// bb.position(m_posShapes[index]);
		bb.position(getPositionForRecord(index));
		bb.order(ByteOrder.LITTLE_ENDIAN);

		// /bb.position(bb.position()+4);
		shapeType = bb.getInt();
		// el shape tal con tema tal y númro tal es null
		if (shapeType == SHP.NULL || shapeType > 28) {
			logger.info("El shape =" + index + " de la capa =" + toString()
					+ " es null");
			return null;
		}

		// retrieve that shape.
		// tempRecord.setShape(readShape(tempShapeType, tempContentLength, in));
		switch (type) {
		case SHP.POINT2D:
			p = readPoint(bb);

			return ShapeFactory.createPoint2D(p.getX(), p.getY());

		case SHP.POLYLINE2D:

			// BoundingBox = readRectangle(bb);
			// bb.getDouble();
			// bb.getDouble();
			// bb.getDouble();
			// bb.getDouble();
			bb.position(bb.position() + 32);
			numParts = bb.getInt();
			numPoints = bb.getInt();

			// part indexes.
			// Geometry geom = GeometryFactory.toGeometryArray();
			GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
					numPoints);

			int[] tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bb.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bb);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < numParts - 1) {
						j++;
					}
				} else {
					elShape.lineTo(p.x, p.y);
				}
			}

			return ShapeFactory.createPolyline2D(elShape);

		case SHP.POLYGON2D:

			// BoundingBox = readRectangle(bb);
			bb.getDouble();
			bb.getDouble();
			bb.getDouble();
			bb.getDouble();

			numParts = bb.getInt();

			numPoints = bb.getInt();

			// part indexes.
			// Geometry geom = GeometryFactory.toGeometryArray();
			elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, numPoints);

			tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bb.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bb);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < numParts - 1) {
						j++;
					}
				} else {
					if (i == numPoints - 1) {
						elShape.closePath();
					} else {
						elShape.lineTo(p.x, p.y);
					}
				}
			}

			return ShapeFactory.createPolygon2D(elShape);

		case SHP.POINT3D:

			double x = bb.getDouble();
			double y = bb.getDouble();
			double z = bb.getDouble();

			return ShapeFactory.createPoint3D(x, y, z);

		case SHP.POINTM:

			double x1 = bb.getDouble();
			double y1 = bb.getDouble();
			double m1 = bb.getDouble();

			return ShapeMFactory.createPoint2DM(x1, y1, m1);

		case SHP.POLYLINE3D:
			bb.position(bb.position() + 32);
			numParts = bb.getInt();
			numPoints = bb.getInt();
			elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, numPoints);
			tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bb.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bb);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < numParts - 1) {
						j++;
					}
				} else {
					elShape.lineTo(p.x, p.y);
				}
			}
			// read zRange
			double[] boxZ = new double[2];
			boxZ[0] = bb.getDouble();
			boxZ[1] = bb.getDouble();

			// read zArray
			double[] pZ = new double[numPoints];
			for (i = 0; i < numPoints; i++) {
				pZ[i] = bb.getDouble();
			}
			// return ShapeFactory.createPolyline3D(elShape, pZ);
			// ------------ Piece modified -----------------
			// read mRange
			double[] boxM1 = new double[2];
			boxM1[0] = bb.getDouble();// min
			boxM1[1] = bb.getDouble();// max

			// read mArray
			double[] pM1 = new double[numPoints];
			for (i = 0; i < numPoints; i++) {
				// m is optional. If "no data" --> (z < -10^-38)
				pM1[i] = bb.getDouble();
				// System.out.println("m: " + pM1[i]);
			}
			return ShapeZMFactory.createPolyline3DM(elShape, pZ, pM1);
			// -------------------------------------
		case (SHP.POLYLINEM):
			bb.position(bb.position() + 32);
			numParts = bb.getInt();
			numPoints = bb.getInt();
			elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, numPoints);
			tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bb.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bb);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < (numParts - 1)) {
						j++;
					}
				} else {
					elShape.lineTo(p.x, p.y);
				}
			}

			double[] boxM = new double[2];
			boxM[0] = bb.getDouble();
			boxM[1] = bb.getDouble();

			double[] pM = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				pM[i] = bb.getDouble();
			}

			return ShapeMFactory.createPolyline2DM(elShape, pM);

		case SHP.POLYGON3D:
			bb.position(bb.position() + 32);
			numParts = bb.getInt();
			numPoints = bb.getInt();
			elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, numPoints);
			tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bb.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bb);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < numParts - 1) {
						j++;
					}
				} else {
					if (i == numPoints - 1) {
						elShape.closePath();
					} else {
						elShape.lineTo(p.x, p.y);
					}
				}
			}

			double[] boxpoZ = new double[2];
			boxpoZ[0] = bb.getDouble();
			boxpoZ[1] = bb.getDouble();

			double[] poZ = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				poZ[i] = bb.getDouble();
			}

			return ShapeFactory.createPolygon3D(elShape, poZ);

		case SHP.POLYGONM:
			bb.position(bb.position() + 32);
			numParts = bb.getInt();
			numPoints = bb.getInt();
			elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, numPoints);
			tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bb.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bb);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < numParts - 1) {
						j++;
					}
				} else {
					elShape.lineTo(p.x, p.y);
				}
			}

			double[] boxpoM = new double[2];
			boxpoM[0] = bb.getDouble();
			boxpoM[1] = bb.getDouble();

			double[] poM = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				poM[i] = bb.getDouble();
			}

			return ShapeMFactory.createPolygon2DM(elShape, poM);

		case SHP.MULTIPOINT2D:
			bb.position(bb.position() + 32);
			numPoints = bb.getInt();

			double[] tempX = new double[numPoints];
			double[] tempY = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				tempX[i] = bb.getDouble();
				tempY[i] = bb.getDouble();
			}

			return ShapeFactory.createMultipoint2D(tempX, tempY);

		case SHP.MULTIPOINT3D:
			bb.position(bb.position() + 32);
			numPoints = bb.getInt();

			double[] temX = new double[numPoints];
			double[] temY = new double[numPoints];
			double[] temZ = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				temX[i] = bb.getDouble();
				temY[i] = bb.getDouble();
				// temZ[i] = bb.getDouble();
			}

			for (i = 0; i < numPoints; i++) {
				temZ[i] = bb.getDouble();
			}
			return ShapeFactory.createMultipoint3D(temX, temY, temZ);

		case SHP.MULTIPOINTM:
			bb.position(bb.position() + 32);
			numPoints = bb.getInt();

			double[] temXM = new double[numPoints];
			double[] temYM = new double[numPoints];
			double[] temM = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				temXM[i] = bb.getDouble();
				temYM[i] = bb.getDouble();
				// temZ[i] = bb.getDouble();
			}

			for (i = 0; i < numPoints; i++) {
				temM[i] = bb.getDouble();
			}
			return ShapeMFactory.createMultipoint2DM(temXM, temYM, temM);
		}

		return null;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#getShapeCount()
	 */
	public int getShapeCount() throws ReadDriverException {
		return numReg;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShapeType()
	 */
	public int getShapeType() {
		int auxType = 0;

		// Modified to support 4D shapefiles ------------
		switch (type) {
		case SHP.POINT2D:
			auxType = auxType | FShape.POINT;
			break;
		case SHP.POINTM:
			auxType = auxType | FShape.POINT | FShape.M;
			break;
		case SHP.POINT3D:
			auxType = auxType | FShape.POINT | FShape.Z;
			break;

		case SHP.POLYLINE2D:
			auxType = auxType | FShape.LINE;
			break;
		case SHP.POLYLINEM:
			auxType = auxType | FShape.LINE | FShape.M;
			break;
		case SHP.POLYLINE3D:
			auxType = auxType | FShape.LINE | FShape.Z;
			break;

		case SHP.POLYGON2D:
			auxType = auxType | FShape.POLYGON;
			break;
		case SHP.POLYGONM:
			auxType = auxType | FShape.POLYGON | FShape.M;
			break;
		case SHP.POLYGON3D:
			auxType = auxType | FShape.POLYGON | FShape.Z;
			break;

		case SHP.MULTIPOINT2D:
			auxType = auxType | FShape.MULTIPOINT;
			break;
		case SHP.MULTIPOINTM:
			auxType = auxType | FShape.MULTIPOINT | FShape.M;
			break;
		case SHP.MULTIPOINT3D:
			auxType = auxType | FShape.MULTIPOINT | FShape.Z;
			break;
		}
		// ------------------------------------------------

		return auxType;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#initialize()
	 */
	public void initialize() throws InitializeDriverException {
		// create a new header.
		ShapeFileHeader2 myHeader = new ShapeFileHeader2();

		bb.position(0);

		// read the header
		myHeader.readHeader(bb);

		extent = new Rectangle2D.Double(myHeader.myXmin, myHeader.myYmin,
				myHeader.myXmax - myHeader.myXmin, myHeader.myYmax
						- myHeader.myYmin);

		type = myHeader.myShapeType;

		double x = myHeader.myXmin;
		double y = myHeader.myYmin;
		double w = myHeader.myXmax - myHeader.myXmin;
		double h = myHeader.myYmax - myHeader.myYmin;

		if (w == 0) {
			x -= 0.1;
			w = 0.2;
		}

		if (h == 0) {
			y -= 0.1;
			h = 0.2;
		}

		// String strFichDbf = m_Path.toLowerCase().replaceAll("\\.shp",
		// ".dbf");
		String strFichDbf = fileShp.getAbsolutePath();
		strFichDbf = strFichDbf.replaceAll("\\.shp *\\Z", ".dbf");
		strFichDbf = strFichDbf.replaceAll("\\.SHP *\\Z", ".DBF");

		DbaseFileNIO m_FichDbf = new DbaseFileNIO();

		try {
			m_FichDbf.open(new File(strFichDbf));
		} catch (IOException e) {
			throw new FileNotFoundDriverException(getName(), e, strFichDbf);
		}
		numReg = m_FichDbf.getRecordCount();
		charset = m_FichDbf.getCharSet();
		// shpWriter.initialize(file);
		// m_posShapes = new long[numReg];

		// read the records.
		/*
		 * int tempCurrentLength = myHeader.getHeaderLength(); int numReg = 0;
		 * 
		 * long pos1;
		 * 
		 * while (tempCurrentLength < myHeader.myFileLength) { // read the
		 * record header // ShapeFileRecord tempRecord = new ShapeFileRecord();
		 * // Bytes 0 to 4 represent the record number in the file, these may be
		 * out of order. bb.order(ByteOrder.BIG_ENDIAN);
		 * 
		 * // tempRecord.setIndex(in.readInt()); bb.getInt();
		 * 
		 * // read the content length of this record in 16 bit words, excluding
		 * the index. // in.setLittleEndianMode(false); int tempContentLength =
		 * bb.getInt();
		 * 
		 * pos1 = bb.position();
		 * 
		 * m_posShapes[numReg] = bb.position();
		 * 
		 * // Posicionamos bb.position((pos1 + (2 * tempContentLength))); numReg
		 * = numReg + 1;
		 * 
		 * // update the current length the 4 is for the index, and content
		 * length. tempCurrentLength = tempCurrentLength + 4 +
		 * tempContentLength; }
		 */
	}

	/**
	 * Reads the Point from the shape file.
	 * 
	 * @param in
	 *            ByteBuffer.
	 * 
	 * @return Point2D.
	 */
	private synchronized Point2D.Double readPoint(BigByteBuffer2 in) {
		// create a new point
		Point2D.Double tempPoint = new Point2D.Double();

		// bytes 1 to 4 are the type and have already been read.
		// bytes 4 to 12 are the X coordinate
		in.order(ByteOrder.LITTLE_ENDIAN);
		tempPoint.setLocation(in.getDouble(), in.getDouble());

		return tempPoint;
	}

	/**
	 * Lee un rectángulo del fichero.
	 * 
	 * @param in
	 *            ByteBuffer.
	 * 
	 * @return Rectángulo.
	 * 
	 * @throws IOException
	 */
	private synchronized Rectangle2D.Double readRectangle(BigByteBuffer2 in) {
		Rectangle2D.Double tempRect = new Rectangle2D.Double();
		in.order(ByteOrder.LITTLE_ENDIAN);
		tempRect.setFrameFromDiagonal(in.getDouble(), in.getDouble(),
				in.getDouble(), in.getDouble());

		if (tempRect.width == 0) {
			tempRect.width = 0.2;
			tempRect.x -= 0.1;
		}

		if (tempRect.height == 0) {
			tempRect.height = 0.2;
			tempRect.y -= 0.1;
		}

		return tempRect;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#getFullExtent()
	 */
	public Rectangle2D getFullExtent() throws ReadDriverException {
		return extent;
	}

	/**
	 * Obtiene el extent del shape a partir de un índice.
	 * 
	 * @param index
	 *            Índice.
	 * 
	 * @return Rectángulo.
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.BoundedShapes#getShapeBounds()
	 */
	public synchronized Rectangle2D getShapeBounds(int index)
			throws ReadDriverException {
		Point2D p = new Point2D.Double();
		Rectangle2D BoundingBox = new Rectangle2D.Double();
		try {
			bb.position(getPositionForRecord(index));
		} catch (Exception e) {
			logger.error(" Shapefile is corrupted. Drawing aborted. =" + e
					+ "  " + "index = " + index);
		}
		if (bb == null)
			throw new RuntimeException(
					"El fichero está cerrado. Revise los start() - stop() de FileDataSourceAdapater");
		else {
			bb.order(ByteOrder.LITTLE_ENDIAN);
		}

		int tipoShape = bb.getInt();

		// AZABALA: si tipoShape viene con valores erroneos deja de funcionar
		// el metodo getShape(i)
		// if (tipoShape != SHP.NULL) {
		// type = tipoShape;
		//
		// }

		// retrieve that shape.
		// tempRecord.setShape(readShape(tempShapeType, tempContentLength, in));
		switch (tipoShape) {
		case SHP.POINT2D:
		case SHP.POINT3D:
		case SHP.POINTM:
			p = readPoint(bb);
			BoundingBox = new Rectangle2D.Double(p.getX() - 0.1,
					p.getY() - 0.1, 0.2, 0.2);

			break;

		case SHP.POLYLINE2D:
		case SHP.POLYGON2D:
		case SHP.MULTIPOINT2D:
		case SHP.POLYLINE3D:
		case SHP.POLYGON3D:
		case SHP.MULTIPOINT3D:
		case SHP.POLYLINEM:
		case SHP.POLYGONM:
		case SHP.MULTIPOINTM:

			// BoundingBox
			BoundingBox = readRectangle(bb);

			break;
		}

		return BoundingBox;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#accept(java.io.File)
	 */
	public boolean accept(File f) {
		return f.getName().toUpperCase().endsWith("SHP");
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getType()
	 */
	public String getName() {
		// Para que se use este driver en lugar del viejo DemoShpDriver.

		// return "gvSIG indexed shp driver";
		return "gvSIG shp driver";
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#getDataDriverName()
	 */
	public String getDataDriverName() {
		return "gdbms dbf driver";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.BoundedShapes#getShapeType(int)
	 */
	public int getShapeType(int index) {
		// Por ahora todos los fichero .shp contienen
		// entidades del mismo tipo. Si trabajamos con
		// alguno mixto, tendremos que cambiar esta función.
		return getShapeType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getDriverAttributes()
	 */
	public DriverAttributes getDriverAttributes() {
		return null;
	}

	private synchronized long getPositionForRecord(int numRec) {
		// shx file has a 100 bytes header, then, records
		// 8 bytes length, one for each entity.
		// first 4 bytes are the offset
		// next 4 bytes are length

		int posIndex = 100 + numRec * 8;
		// bbShx.position(posIndex);
		long pos = 8 + 2 * bbShx.getInt(posIndex);

		return pos;
	}

	public File getFile() {
		return fileShp;
	}

	public void reload() throws ReloadDriverException {
		try {
			open(fileShp);
			initialize();
		} catch (OpenDriverException e) {
			throw new ReloadDriverException(getName(), e);
		} catch (InitializeDriverException e) {
			throw new ReloadDriverException(getName(), e);
		}
	}

	public boolean canWriteGeometry(int gvSIGgeometryType) {
		return shpWriter.canWriteGeometry(gvSIGgeometryType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.edition.IWriter#preProcess()
	 */
	public void preProcess() throws StartWriterVisitorException {
		// Necesitamos crear un fichero temporal porque a la vez que esic
		// La cabecera ya existe, así que no la sobreescribimos.
		// shpWriter.setWriteHeaders(false);
		shpWriter.preProcess();

	}

	public void process(IRowEdited row) throws ProcessWriterVisitorException {
		shpWriter.process(row);

	}

	public void postProcess() throws StopWriterVisitorException {
		shpWriter.postProcess();

		try {

			// close();

			// Shp
			FileChannel fcinShp = new FileInputStream(fTemp).getChannel();
			FileChannel fcoutShp = new FileOutputStream(fileShp).getChannel();
			DriverUtilities.copy(fcinShp, fcoutShp);

			// Shx
			File shxFile = SHP.getShxFile(fTemp);
			FileChannel fcinShx = new FileInputStream(shxFile).getChannel();
			FileChannel fcoutShx = new FileOutputStream(SHP.getShxFile(fileShp))
					.getChannel();
			DriverUtilities.copy(fcinShx, fcoutShx);

			// Dbf
			File dbfFile = getDataFile(fTemp);

			short originalEncoding = DbfEncodings.getInstance()
					.getDbfIdForCharset(shpWriter.getCharset());
			RandomAccessFile fo = new RandomAccessFile(dbfFile, "rw");
			fo.seek(29);
			fo.writeByte(originalEncoding);
			fo.close();

			FileChannel fcinDbf = new FileInputStream(dbfFile).getChannel();
			FileChannel fcoutDbf = new FileOutputStream(getDataFile(fileShp))
					.getChannel();
			DriverUtilities.copy(fcinDbf, fcoutDbf);

			// Borramos los temporales
			fTemp.delete();
			shxFile.delete();
			dbfFile.delete();
			reload();

		} catch (FileNotFoundException e) {
			throw new StopWriterVisitorException(getName(), e);
		} catch (IOException e) {
			throw new StopWriterVisitorException(getName(), e);
		} catch (ReloadDriverException e) {
			throw new StopWriterVisitorException(getName(), e);
		}

	}

	public String getCapability(String capability) {
		return shpWriter.getCapability(capability);
	}

	public void setCapabilities(Properties capabilities) {
		shpWriter.setCapabilities(capabilities);

	}

	public boolean canWriteAttribute(int sqlType) {
		return shpWriter.canWriteAttribute(sqlType);
	}

	public ShpWriter getShpWriter() {
		return shpWriter;
	}

	public void setShpWriter(ShpWriter shpWriter) {
		this.shpWriter = shpWriter;
	}

	public void initialize(ITableDefinition layerDef)
			throws InitializeWriterException {
		int aux = (int) (Math.random() * 1000);
		fTemp = new File(tempDirectoryPath + "/tmpShp" + aux + ".shp");
		shpWriter.setFile(fTemp);
		shpWriter.initialize(layerDef);
		shpWriter.setCharset(charset);
	}

	// public void setFlatness(double flatness) {
	// shpWriter.setFlatness(flatness);
	// }

	public boolean isWritable() {
		return fileShp.canWrite();
	}

	public IWriter getWriter() {
		return this;
	}

	public ITableDefinition getTableDefinition() {
		return shpWriter.getTableDefinition();
	}

	public boolean canAlterTable() {
		return true;
	}

	public boolean canSaveEdits() {
		return shpWriter.canSaveEdits();
	}

	public boolean isWriteAll() {
		return true;
	}

	public File getDataFile(File f) {
		return SHP.getDbfFile(f);
	}
}
