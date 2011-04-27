package com.hardcode.gdbms.engine.data.file;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.AbstractDataSource;
import com.hardcode.gdbms.engine.data.driver.ReadAccess;


/**
 * Abstract class for File and Object DataSources
 *
 * @author Fernando González Cortés
 */
public abstract class AbstractFileDataSource extends AbstractDataSource {

    private boolean driverPropertiesAdded;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected abstract ReadAccess getReadDriver();

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getRowCount()
     */
    public long getRowCount() throws ReadDriverException {
    	return getReadDriver().getRowCount();
    }
}
