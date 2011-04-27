package com.hardcode.gdbms.engine.data.object;

import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;


/**
 * Factory to create object DataSources
 */
public interface ObjectDataSource extends DataSource {
    /**
     * Sets the driver layer
     *
     * @param readDriver driver to access the data
     */
    public void setDriver(ObjectDriver readDriver);
}
