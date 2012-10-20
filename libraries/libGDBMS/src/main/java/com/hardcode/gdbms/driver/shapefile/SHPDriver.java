package com.hardcode.gdbms.driver.shapefile;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import com.hardcode.gdbms.driver.dbf.DBFDriver;
import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.SpatialFileDriver;
import com.hardcode.gdbms.engine.data.file.FileDataWare;
import com.hardcode.gdbms.engine.spatial.Geometry;
import com.hardcode.gdbms.engine.spatial.GeometryImpl;
import com.hardcode.gdbms.engine.values.Value;

/**
 *
 */
public class SHPDriver implements SpatialFileDriver {

	private DBFDriver dbfDriver;
	private SHP shp;
	private String prefix;

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.FileDriver#open(java.io.File)
	 */
	public void open(File file) throws OpenDriverException {
		String fileName = file.getAbsolutePath().trim();
		prefix = fileName.substring(0, fileName.length() - 4);
		dbfDriver = new DBFDriver();
		dbfDriver.open(new File(prefix + ".dbf"));
		shp = new SHP();
		shp.open(file, prefix);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.FileDriver#close()
	 */
	public void close() throws CloseDriverException {
		dbfDriver.close();
		shp.close();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.FileDriver#fileAccepted(java.io.File)
	 */
	public boolean fileAccepted(File f) {
		return f.getName().toLowerCase().endsWith(".shp");
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.FileDriver#writeFile(com.hardcode.gdbms.engine.data.file.FileDataWare,
	 *      java.io.File)
	 */
	public void writeFile(FileDataWare dataWare) throws WriteDriverException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		return dbfDriver.getFieldValue(rowIndex, fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return dbfDriver.getFieldCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return dbfDriver.getFieldName(fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		return dbfDriver.getRowCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return dbfDriver.getFieldType(i);
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return "GDBMS shapefile driver";
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.SpatialFileDriver#getFullExtent()
	 */
	public Rectangle2D getFullExtent() {
		return shp.getExtent();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.SpatialDriver#getGeometry(long)
	 */
	public Geometry getGeometry(long rowIndex) throws ReadDriverException {
		try {
			return new GeometryImpl(shp.getGeometry((int) rowIndex));
		} catch (IOException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.SpatialDriver#getJTSGeometry(long)
	 */
	public com.vividsolutions.jts.geom.Geometry getJTSGeometry(long rowIndex)
			throws ReadDriverException {
		try {
			return shp.getGeometry((int) rowIndex).getJTSGeometry();
		} catch (IOException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	public void createSource(String path, String[] fieldNames, int[] fieldTypes)
			throws ReadDriverException {
		throw new UnsupportedOperationException();
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return dbfDriver.getFieldWidth(i);
	}

}
