package com.hardcode.gdbms.engine.data.file;

import java.awt.geom.Rectangle2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.SpatialDataSource;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.SpatialFileDriver;
import com.hardcode.gdbms.engine.spatial.Geometry;

/**
 *
 */
public class FileSpatialDataSourceAdapter extends FileDataSourceAdapter
        implements FileDataSource, SpatialDataSource {

    /**
     * @throws DriverException
     * @see com.hardcode.gdbms.engine.data.SpatialDataSource#getFullExtent()
     */
    public Rectangle2D getFullExtent() throws ReadDriverException {
        return ((SpatialFileDriver) getDriver()).getFullExtent();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.SpatialDataSource#getGeometry(long)
     */
    public Geometry getGeometry(long rowIndex) throws ReadDriverException {
        return ((SpatialFileDriver) getDriver()).getGeometry(rowIndex);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.SpatialDriver#getJTSGeometry(long)
     */
    public com.vividsolutions.jts.geom.Geometry getJTSGeometry(long rowIndex) throws ReadDriverException {
        return ((SpatialFileDriver) getDriver()).getJTSGeometry(rowIndex);
    }

}
