package com.hardcode.gdbms.engine.data.db;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.driver.DBDriver;


/**
 * Interface for database DataSources, allowing query delegation on the
 * original server
 */
public interface DBDataSource extends DataSource {
    /**
     * Executes a query against the server where this data source is stored.
     * It's not neccessary to call between a start and a stop call
     *
     * @param sql instruction to execute
     * @throws ReadDriverException TODO
     */
    public void execute(String sql) throws ReadDriverException;

    /**
     * Sets the driver layer
     *
     * @param driver
     */
    public void setDriver(DBDriver driver);

    /**
     * Gets the driver of the DataSource
     *
     * @return DBDriver
     */
    public Driver getDriver();

    /**
     * Devuelve el dbms de DataSource, o null si no tiene uno solo
     *
     * @return nombre del dbms
     */
    String getDBMS();
}
