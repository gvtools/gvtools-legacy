package com.hardcode.gdbms.engine.data.db;

import java.awt.geom.Rectangle2D;
import java.sql.SQLException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.SpatialDataSource;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.SpatialDBDriver;
import com.hardcode.gdbms.engine.spatial.Geometry;

/**
 *
 */
public class DBSpatialDataSourceAdapter extends DBDataSourceAdapter implements
		DBDataSource, SpatialDataSource {

	public void start() throws ReadDriverException {
		try {
			if (sem == 0) {
				con = getConnection();
				SpatialDBTableSourceInfo info = (SpatialDBTableSourceInfo) getSourceInfo();
				((SpatialDBDriver) driver).open(con, sql, info.tableName,
						info.geometryField);
			}

			sem++;
		} catch (SQLException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	/**
	 * @throws DriverException
	 * @see com.hardcode.gdbms.engine.data.SpatialDataSource#getFullExtent()
	 */
	public Rectangle2D getFullExtent() throws ReadDriverException {
		return ((SpatialDBDriver) getDriver()).getFullExtent();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.SpatialDataSource#getGeometry(long)
	 */
	public Geometry getGeometry(long rowIndex) throws ReadDriverException {
		return ((SpatialDBDriver) getDriver()).getGeometry(rowIndex);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.SpatialDriver#getJTSGeometry(long)
	 */
	public com.vividsolutions.jts.geom.Geometry getJTSGeometry(long rowIndex)
			throws ReadDriverException {
		return ((SpatialDBDriver) getDriver()).getJTSGeometry(rowIndex);
	}

}
