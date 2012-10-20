/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package com.iver.cit.gvsig.fmap.edition;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.DateValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.expansionfile.CloseExpansionFileException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.expansionfile.OpenExpansionFileException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.shp.DbaseFileWriterNIO;
import com.iver.cit.gvsig.fmap.drivers.shp.SHP;
import com.iver.cit.gvsig.fmap.drivers.shp.write.SHPShape;
import com.iver.cit.gvsig.fmap.drivers.shp.write.ShapefileException;
import com.iver.utiles.bigfile.BigByteBuffer2;

/**
 * Implementaci�n en memoria de ExpansionFile.
 * 
 * @author Vicente Caballero Navarro
 */
public class PruebaVicenteExpansionFile2 implements ExpansionFile {
	// ArrayList rows = new ArrayList();
	private static Locale ukLocale = new Locale("en", "UK"); // English, UK
																// version
	private EditableAdapter edAdapter;
	private Charset charset = Charset.forName("ISO-8859-1");
	private DbaseFileWriterNIO.FieldFormatter formatter = new DbaseFileWriterNIO.FieldFormatter();

	private final Number NULL_NUMBER = new Integer(0);
	private final String NULL_STRING = "";
	private final String NULL_DATE = "        ";

	// private WritableByteChannel dbfchannelWriter;
	// private WritableByteChannel shxchannelWriter;

	private SHPShape m_shape = null;

	private FileChannel readChannel;
	private FileChannel shpChannelWriter;
	private FileChannel shxChannelWriter;
	private FileChannel dbfChannelWriter;

	private int m_offset;
	private int m_cnt;
	private ArrayList indexControl = new ArrayList();

	private BigByteBuffer2 bbRead;
	private BigByteBuffer2 bbShxRead;
	private BigByteBuffer2 bbDbfRead;
	private ByteBuffer bbWriter = null;
	private ByteBuffer bbShxWriter = null;

	private FileChannel readChannelShx;
	private ByteBuffer cachedRecordRead = null;

	private int posActual = -1;
	private int recordOffset;
	private byte[] bytesCachedRecord = null;
	private Charset chars;
	private RandomAccessFile raf;
	private MapMode mode;
	private FileInputStream finShx;
	private ByteBuffer dbfBuffer;

	private class Index {
		private int internalIndex;
		private int position;
		private int status;
		private int type;

		public Index(int type, int indexInternalFields, int pos, int status) {
			this.type = type;
			internalIndex = indexInternalFields;
			position = pos;
			this.status = status;
		}

		public int getInternalIndex() {
			return internalIndex;
		}

		public int getPosition() {
			return position;
		}

		public int getStatus() {
			return status;
		}

		public int getType() {
			return type;
		}
	}

	public PruebaVicenteExpansionFile2(EditableAdapter edAdapter) {
		this.edAdapter = edAdapter;
	}

	// BitSet invalidRows = new BitSet();
	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#addRow(IRow, int)
	 */
	public int addRow(IRow row, int status, int indexInternalFields)
			throws ExpansionFileWriteException {
		int newIndex = indexControl.size();
		IRowEdited edRow = new DefaultRowEdited(row, status, newIndex);
		// InternalRow iRow = new InternalRow(edRow, indexInternalFields);
		Value[] values = edRow.getAttributes();
		IGeometry geometry = ((IFeature) edRow.getLinkedRow()).getGeometry();
		int pos = doAddRow(values, geometry);
		indexControl.add(new Index(geometry.getGeometryType(),
				indexInternalFields, pos, status));
		return newIndex;
	}

	/**
	 * Devuelve la geometria a partir de un �ndice.
	 * 
	 * @param index
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public synchronized IGeometry getShape(int position, int index, int type) {
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
		bbRead.position(getPositionForRecord(index));
		bbRead.order(ByteOrder.LITTLE_ENDIAN);

		// /bb.position(bb.position()+4);
		shapeType = bbRead.getInt();
		System.err.println("type= " + shapeType);
		// el shape tal con tema tal y n�mro tal es null
		if (shapeType == SHP.NULL) {
			return null;
		}

		// retrieve that shape.
		// tempRecord.setShape(readShape(tempShapeType, tempContentLength, in));
		switch (type) {
		case (SHP.POINT2D):
			p = readPoint(bbRead);
			System.err.println("Point= " + p.getX() + ", " + p.getY());
			return ShapeFactory.createPoint2D(p.getX(), p.getY());

		case (SHP.POLYLINE2D):

			// BoundingBox = readRectangle(bb);
			// bb.getDouble();
			// bb.getDouble();
			// bb.getDouble();
			// bb.getDouble();
			bbRead.position(bbRead.position() + 32);
			numParts = bbRead.getInt();
			numPoints = bbRead.getInt();

			// part indexes.
			// Geometry geom = GeometryFactory.toGeometryArray();
			GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
					numPoints);

			int[] tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bbRead.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bbRead);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < (numParts - 1)) {
						j++;
					}
				} else {
					elShape.lineTo(p.x, p.y);
				}
			}

			return ShapeFactory.createPolyline2D(elShape);

		case (SHP.POLYGON2D):

			// BoundingBox = readRectangle(bb);
			bbRead.getDouble();
			bbRead.getDouble();
			bbRead.getDouble();
			bbRead.getDouble();

			numParts = bbRead.getInt();

			numPoints = bbRead.getInt();

			// part indexes.
			// Geometry geom = GeometryFactory.toGeometryArray();
			elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, numPoints);

			tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bbRead.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bbRead);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < (numParts - 1)) {
						j++;
					}
				} else {
					elShape.lineTo(p.x, p.y);
				}
			}

			return ShapeFactory.createPolygon2D(elShape);

		case (SHP.POINT3D):

			double x = bbRead.getDouble();
			double y = bbRead.getDouble();
			double z = bbRead.getDouble();

			return ShapeFactory.createPoint3D(x, y, z);

		case (SHP.POLYLINE3D):
			bbRead.position(bbRead.position() + 32);
			numParts = bbRead.getInt();
			numPoints = bbRead.getInt();
			elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, numPoints);
			tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bbRead.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bbRead);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < (numParts - 1)) {
						j++;
					}
				} else {
					elShape.lineTo(p.x, p.y);
				}
			}

			double[] boxZ = new double[2];
			boxZ[0] = bbRead.getDouble();
			boxZ[1] = bbRead.getDouble();

			double[] pZ = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				pZ[i] = bbRead.getDouble();
			}

			return ShapeFactory.createPolyline3D(elShape, pZ);
		case (SHP.POLYGON3D):
			bbRead.position(bbRead.position() + 32);
			numParts = bbRead.getInt();
			numPoints = bbRead.getInt();
			elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD, numPoints);
			tempParts = new int[numParts];

			for (i = 0; i < numParts; i++) {
				tempParts[i] = bbRead.getInt();
			}

			j = 0;

			for (i = 0; i < numPoints; i++) {
				p = readPoint(bbRead);

				if (i == tempParts[j]) {
					elShape.moveTo(p.x, p.y);

					if (j < (numParts - 1)) {
						j++;
					}
				} else {
					elShape.lineTo(p.x, p.y);
				}
			}

			double[] boxpoZ = new double[2];
			boxpoZ[0] = bbRead.getDouble();
			boxpoZ[1] = bbRead.getDouble();

			double[] poZ = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				poZ[i] = bbRead.getDouble();
			}

			return ShapeFactory.createPolygon3D(elShape, poZ);

		case (SHP.MULTIPOINT2D):
			bbRead.position(bbRead.position() + 32);
			numPoints = bbRead.getInt();

			double[] tempX = new double[numPoints];
			double[] tempY = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				tempX[i] = bbRead.getDouble();
				tempY[i] = bbRead.getDouble();
			}

			return ShapeFactory.createMultipoint2D(tempX, tempY);

		case (SHP.MULTIPOINT3D):
			bbRead.position(bbRead.position() + 32);
			numPoints = bbRead.getInt();

			double[] temX = new double[numPoints];
			double[] temY = new double[numPoints];
			double[] temZ = new double[numPoints];

			for (i = 0; i < numPoints; i++) {
				temX[i] = bbRead.getDouble();
				temY[i] = bbRead.getDouble();
				// temZ[i] = bb.getDouble();
			}

			for (i = 0; i < numPoints; i++) {
				temZ[i] = bbRead.getDouble();
			}
			return ShapeFactory.createMultipoint3D(temX, temY, temZ);
		}

		return null;
	}

	private int doAddRow(Value[] values, IGeometry geometry) {
		try {
			write(values);
			return writeIGeometry(geometry);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ShapefileException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int modifyRow(int index, IRow row, int indexInternalFields)
			throws ExpansionFileWriteException {
		// InternalRow iOldRow = (InternalRow) rows.get(index);
		// IRowEdited edRow = new DefaultRowEdited(row,
		// iOldRow.getRow().getStatus(), index);
		//
		// InternalRow iRow = new InternalRow(edRow, indexInternalFields);
		// rows.add(iRow);
		//
		//
		// return rows.size() - 1;
		return -1;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#getRow(int)
	 */
	public IRowEdited getRow(int index) throws ExpansionFileReadException {
		// Index indexControl=(Index)this.index.get(index);
		IRowEdited row = doGetRow(index);
		return row;
		// InternalRow iRow = (InternalRow) rows.get(index);
		// int indexInternalFields = indexControl.getInternalIndex();
		// return edAdapter.createExternalRow(iRow.getRow(),
		// indexInternalFields);
		// return iRow.getRow();

	}

	private IRowEdited doGetRow(int index) {
		Index indexControl = (Index) this.indexControl.get(index);
		IGeometry geometry = getShape(indexControl.getPosition(),
				indexControl.getInternalIndex(), indexControl.getType());
		FieldDescription[] fds = edAdapter.getFieldsDescription();
		int length = fds.length;
		Value[] values = new Value[length];
		int lengthRow = 0;
		for (int i = 0; i < fds.length; i++) {
			lengthRow += fds[i].getFieldLength();
		}
		for (int i = 0; i < length; i++) {
			try {
				values[i] = getFieldValue(indexControl.getInternalIndex(), i,
						lengthRow);
			} catch (DriverException e) {
				e.printStackTrace();
			}
		}
		DefaultFeature fea = new DefaultFeature(geometry, values);
		IRowEdited edRow = new DefaultRowEdited(fea, indexControl.getStatus(),
				index);
		return edRow;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#invalidateRow(int)
	 */
	/*
	 * public void invalidateRow(int index) { invalidRows.set(index, true); }
	 */
	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#compact()
	 */
	public void compact(HashMap relations) {
		/*
		 * ArrayList geoAux = new ArrayList(); Iterator iter =
		 * relations.keySet().iterator(); HashMap aux = new HashMap(); int n =
		 * 0;
		 * 
		 * while (iter.hasNext()) { Integer virtualIndex = (Integer)
		 * iter.next(); Integer expansionIndex = (Integer)
		 * relations.get(virtualIndex);
		 * 
		 * if (!invalidRows.get(expansionIndex.intValue())){
		 * geoAux.add(rows.get(expansionIndex.intValue())); aux.put(new
		 * Integer(n), new Integer(geoAux.size()-1)); n++; } }
		 * 
		 * invalidRows.clear(); rows = geoAux; relations.clear();
		 * relations.putAll(aux);
		 */

	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#getRowCount()
	 */
	/*
	 * public int getRowCount() { return rows.size() -
	 * invalidRows.cardinality(); }
	 */
	public void deleteLastRow() {
		// rows.remove(rows.size()-1);
	}

	public void open() throws OpenExpansionFileException {
		initDBFBuffer();
		try {
			shpChannelWriter = (FileChannel) getWriteChannel("c:/pruebaVicente.shp");

			dbfChannelWriter = (FileChannel) getWriteChannel("c:/pruebaVicente.dbf");
			shxChannelWriter = (FileChannel) getWriteChannel("c:/pruebaVicente.shx");
			File file = new File("c:/pruebaVicente.dbf");
			if (file.canWrite()) {
				try {
					raf = new RandomAccessFile(file, "rw");
					mode = FileChannel.MapMode.READ_WRITE;
				} catch (FileNotFoundException e) {
					raf = new RandomAccessFile(file, "r");
					mode = FileChannel.MapMode.READ_ONLY;
				}
			} else {
				raf = new RandomAccessFile(file, "r");
				mode = FileChannel.MapMode.READ_ONLY;
			}
			readChannel = raf.getChannel();

			bbRead = new BigByteBuffer2(readChannel,
					FileChannel.MapMode.READ_ONLY);
			finShx = new FileInputStream(getShxFile(file));
			bbDbfRead = new BigByteBuffer2(readChannel, mode);
			// Open the file and then get a channel from the stream
			readChannelShx = finShx.getChannel();
			bbShxRead = new BigByteBuffer2(readChannelShx,
					FileChannel.MapMode.READ_ONLY);
			bbShxRead.order(ByteOrder.BIG_ENDIAN);

			chars = Charset.forName("ISO-8859-1");
		} catch (IOException e1) {
			throw new OpenExpansionFileException("", e1);
		}
	}

	public File getShxFile(File f) {
		String str = f.getAbsolutePath();

		return new File(str.substring(0, str.length() - 3) + "shx");
	}

	public void close() throws CloseExpansionFileException {
		try {
			bbDbfRead = null;
			dbfBuffer = null;
			dbfChannelWriter.close();
			m_shape = null;
			bbShxWriter = null;
			shpChannelWriter.close();
			shxChannelWriter.close();
			shpChannelWriter = null;
			shxChannelWriter = null;
			indexControl.clear();
			readChannel.close();
			readChannelShx.close();
		} catch (IOException e) {
			throw new CloseExpansionFileException("", e);
		}
		// rows.clear();
		System.gc();
	}

	public int getSize() {
		return indexControl.size();
	}

	public void write(Object[] record) throws IOException {
		FieldDescription[] fds = edAdapter.getFieldsDescription();
		dbfBuffer.position(0);

		// put the 'not-deleted' marker
		dbfBuffer.put((byte) ' ');

		for (int i = 0; i < fds.length; i++) {
			String fieldString = fieldString(record[i], i);
			// if ( header.getFieldLength(i) != fieldString.getBytes().length) {
			// System.out.println(i + " : " + header.getFieldName(i));
			// }

			dbfBuffer.put(fieldString.getBytes(charset.name()));

		}

		write();
	}

	private void write() throws IOException {
		dbfBuffer.position(0);
		int r = dbfBuffer.remaining();
		while ((r -= dbfChannelWriter.write(dbfBuffer)) > 0) {
			; // do nothing
		}
	}

	private void initDBFBuffer() {
		FieldDescription[] fds = edAdapter.getFieldsDescription();
		int tempLength = -1;
		for (int i = 0; i < fds.length; i++) {
			tempLength += fds[i].getFieldLength() + 1;
		}
		dbfBuffer = ByteBuffer.allocateDirect(tempLength);
		bytesCachedRecord = new byte[tempLength];
	}

	private String fieldString(Object obj, final int col) {
		FieldDescription[] fds = edAdapter.getFieldsDescription();
		String o;
		final int fieldLen = fds[col].getFieldLength();
		switch (fds[col].getFieldType()) {
		case Types.VARCHAR:
			o = formatter.getFieldString(fieldLen,
					(obj instanceof NullValue) ? NULL_STRING
							: ((StringValue) obj).getValue());
			break;
		case Types.BOOLEAN:
			o = (obj instanceof NullValue) ? "F" : ((BooleanValue) obj)
					.getValue() == true ? "T" : "F";
			break;
		case Types.DOUBLE:
		case Types.INTEGER:
		case Types.FLOAT:
			Number number = null;
			if (obj instanceof NullValue) {
				number = NULL_NUMBER;
			} else {
				NumericValue gVal = (NumericValue) obj;
				number = new Double(gVal.doubleValue());
			}
			o = formatter.getFieldString(fieldLen,
					fds[col].getFieldDecimalCount(), number);
			break;
		case Types.DATE:
			if (obj instanceof NullValue)
				o = NULL_DATE;
			else
				o = formatter.getFieldString(((DateValue) obj).getValue());
			break;
		default:
			throw new RuntimeException("Unknown type "
					+ fds[col].getFieldType());
		}

		return o;
	}

	private WritableByteChannel getWriteChannel(String path) throws IOException {
		WritableByteChannel channel;

		File f = new File(path);

		if (!f.exists()) {
			System.out.println("Creando fichero " + f.getAbsolutePath());

			if (!f.createNewFile()) {
				throw new IOException("Cannot create file " + f);
			}
		}

		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		channel = raf.getChannel();

		return channel;
	}

	public int writeIGeometry(IGeometry g) throws IOException,
			ShapefileException {
		int shapeType = getShapeType(g.getGeometryType());
		m_shape = SHP.create(shapeType);
		// m_shape.setFlatness(flatness);
		return writeGeometry(g, shapeType);
	}

	public synchronized int writeGeometry(IGeometry g, int type)
			throws IOException {
		if (bbWriter == null) {
			allocateBuffers();
			m_offset = 50;
			m_cnt = 0;

			shpChannelWriter.position(0);
			shxChannelWriter.position(0);

			// throw new IOException("Must write headers first");
		}

		int posInit = m_offset;
		m_shape.obtainsPoints(g);
		int length = m_shape.getLength(g);

		// must allocate enough for shape + header (2 ints)
		checkShapeBuffer(length + 8);

		length /= 2;

		bbWriter.order(ByteOrder.BIG_ENDIAN);
		bbWriter.putInt(++m_cnt);
		bbWriter.putInt(length);
		bbWriter.order(ByteOrder.LITTLE_ENDIAN);
		bbWriter.putInt(type);
		m_shape.write(bbWriter, g);

		// write to the shx
		bbShxWriter.putInt(m_offset);
		bbShxWriter.putInt(length);
		m_offset += (length + 4);
		drain();
		// /assert(m_bb.position() == 0);
		return posInit; // Devolvemos hasta donde hemos escrito
	}

	private void allocateBuffers() {
		bbWriter = ByteBuffer.allocateDirect(16 * 1024);
		bbShxWriter = ByteBuffer.allocateDirect(100);
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
		if (geometryType >= FShape.Z) {
			switch (geometryType - FShape.Z) {
			case (FShape.POINT):
				return FConstant.SHAPE_TYPE_POINTZ;

			case (FShape.LINE):
				return FConstant.SHAPE_TYPE_POLYLINEZ;

			case FShape.POLYGON:
				return FConstant.SHAPE_TYPE_POLYGONZ;

			case FShape.MULTIPOINT:
				return FConstant.SHAPE_TYPE_MULTIPOINTZ; // TODO falta aclarar
															// cosas aqu�.
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
														// cosas aqu�.
			}
		}
		return FConstant.SHAPE_TYPE_NULL;
	}

	/**
	 * Make sure our buffer is of size.
	 * 
	 * @param size
	 *            DOCUMENT ME!
	 */
	private void checkShapeBuffer(int size) {
		if (bbWriter.capacity() < size) {
			bbWriter = ByteBuffer.allocateDirect(size);
		}
	}

	/**
	 * Drain internal buffers into underlying channels.
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	private void drain() throws IOException {
		bbWriter.flip();
		bbShxWriter.flip();

		while (bbWriter.remaining() > 0)
			shpChannelWriter.write(bbWriter);

		while (bbShxWriter.remaining() > 0)
			shxChannelWriter.write(bbShxWriter);

		bbWriter.flip().limit(bbWriter.capacity());
		bbShxWriter.flip().limit(bbShxWriter.capacity());
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

	private synchronized long getPositionForRecord(int numRec) {
		// shx file has a 100 bytes header, then, records
		// 8 bytes length, one for each entity.
		// first 4 bytes are the offset
		// next 4 bytes are length

		int posIndex = 100 + (numRec * 8);
		// bbShx.position(posIndex);
		long pos = 8 + 2 * bbShxRead.getInt(posIndex);

		return pos;
	}

	public Value getFieldValue(long rowIndex, int fieldId, int position)
			throws DriverException {
		FieldDescription[] fds = edAdapter.getFieldsDescription();
		position *= rowIndex;
		for (int i = 0; i < fieldId; i++) {
			position += fds[i].getFieldLength();
		}
		int fieldType = fds[fieldId].getFieldType();
		// Field Type (C or M)
		// char fieldType = fieldTypes[fieldId];

		if (fieldType == Types.BOOLEAN) {
			return ValueFactory.createValue(getBooleanFieldValue(
					(int) rowIndex, fieldId));

			/*
			 * }else if (fieldType == 'N'){ String strValue =
			 * dbf.getStringFieldValue(rowIndex, fieldId); long value =
			 * Long.parseLong(strValue); if ((value > Integer.MIN_VALUE) &&
			 * (value < Integer.MAX_VALUE)){ return new IntValue((int) value);
			 * }else{ return new LongValue(value); }
			 */
		} else if ((fieldType == Types.DOUBLE) || (fieldType == Types.FLOAT)) {
			String strValue;
			try {
				strValue = getStringFieldValue((int) rowIndex, fieldId,
						position).trim();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				throw new DriverException(e1);
			}

			if (strValue.length() == 0) {
				return null;
			}
			double value = 0;
			try {
				value = Double.parseDouble(strValue);
			} catch (Exception e) {
				return ValueFactory.createValue(0D);
			}
			return ValueFactory.createValue(value);
		} else if (fieldType == Types.VARCHAR) {
			try {
				return ValueFactory.createValue(getStringFieldValue(
						(int) rowIndex, fieldId, position).trim());
			} catch (UnsupportedEncodingException e) {
				throw new DriverException(e);
			}
		} else if (fieldType == Types.DATE) {
			String date;
			try {
				date = getStringFieldValue((int) rowIndex, fieldId, position)
						.trim();
			} catch (UnsupportedEncodingException e1) {
				throw new DriverException(e1);
			}
			// System.out.println(rowIndex + " data=" + date);
			if (date.length() == 0) {
				return null;
			}

			String year = date.substring(0, 4);
			String month = date.substring(4, 6);
			String day = date.substring(6, 8);
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
					ukLocale);
			/*
			 * Calendar c = Calendar.getInstance(); c.clear();
			 * c.set(Integer.parseInt(year), Integer.parseInt(month),
			 * Integer.parseInt(day)); c.set(Calendar.MILLISECOND, 0);
			 */
			String strAux = month + "/" + day + "/" + year;
			Date dat;
			try {
				dat = df.parse(strAux);
			} catch (ParseException e) {
				throw new DriverException("Bad Date Format");
			}

			// System.out.println("numReg = " + rowIndex + " date:" +
			// dat.getTime());

			return ValueFactory.createValue(dat);
		} else {
			throw new DriverException("Unknown field type");
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * @param fieldId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean getBooleanFieldValue(int rowIndex, int fieldId) {
		FieldDescription[] fds = edAdapter.getFieldsDescription();
		int recordOffset = (fds.length * rowIndex) + 0;

		// Se calcula el offset del campo
		int fieldOffset = 0;

		for (int i = 0; i < (fieldId - 1); i++) {
			fieldOffset += fds[i].getFieldLength();
		}

		bbDbfRead.position(recordOffset + fieldOffset);

		char bool = (char) bbDbfRead.get();

		return ((bool == 't') || (bool == 'T') || (bool == 'Y') || (bool == 'y'));
	}

	public String getStringFieldValue(int rowIndex, int fieldId, int position)
			throws UnsupportedEncodingException {
		FieldDescription[] fds = edAdapter.getFieldsDescription();
		int fieldOffset = position;
		byte[] data = new byte[fds[fieldId].getFieldLength()];
		if (rowIndex != posActual) {
			recordOffset = (fds.length * rowIndex) + 0;

			/*
			 * System.err.println("getStringFieldValue: rowIndex = " +
			 * rowIndex); System.err.println("recordOffset = " + recordOffset +
			 * " fieldOffset=" + fieldOffset);
			 */
			bbDbfRead.position(recordOffset);
			bbDbfRead.get(bytesCachedRecord);
			cachedRecordRead = ByteBuffer.wrap(bytesCachedRecord);
			posActual = rowIndex;

		}
		cachedRecordRead.position(fieldOffset);
		cachedRecordRead.get(data);

		return new String(data, chars.name());

	}
}
