package com.hardcode.gdbms.engine.data.driver;

import java.awt.geom.Rectangle2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.spatial.Geometry;

/**
 * Driver to access spatial data sources
 */
public interface SpatialDriver {
	/**
	 * Gets the full extent of the data accessed
	 * 
	 * @return Rectangle2D
	 * 
	 * @throws DriverException
	 *             if the operation fails
	 * @throws ReadDriverException
	 */
	public Rectangle2D getFullExtent() throws ReadDriverException;

	/**
	 * gets the rowIndex-th geometry
	 * 
	 * @param rowIndex
	 *            index of the geometry to be retrieved
	 * 
	 * @return Gemetry
	 * @throws ReadDriverException
	 *             TODO
	 */
	public Geometry getGeometry(long rowIndex) throws ReadDriverException;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * @throws ReadDriverException
	 *             TODO
	 */
	public com.vividsolutions.jts.geom.Geometry getJTSGeometry(long rowIndex)
			throws ReadDriverException;
}
