package com.hardcode.gdbms.engine.data.db;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.DBDriver;
import com.hardcode.gdbms.engine.data.driver.DBTransactionalDriver;
import com.hardcode.gdbms.engine.data.edition.DirectDataWare;


/**
 * Factory to create database DataSources
 */
public class DBDataSourceFactory {
    /**
     * Creates a new DBDataSource
     *
     * @return DataBaseDataSource
     */
    public static DBDataSource newDataSourceInstance() {
        return new DBDataSourceAdapter();
    }

    public static DBDataSource newSpatialDataSourceInstance() {
        return new DBSpatialDataSourceAdapter();
    }
    
    /**
     * Creates a new DataWare suitable for the specified driver
     *
     * @param driver DBDriver
     *
     * @return DataWare
     */
    public static DBDataWare newDataWareInstance(DBDriver driver, int mode) {
        if (driver instanceof DBTransactionalDriver) {
            if (mode == DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER){
                return new CoherentRowOrderDataWare();
            }else{
            	DBDataWare directDataware = new DirectDataWare();
//            	directDataware.setSourceInfo(DataSourceFactory.)
                return directDataware;
            }
        } else {
            return new FakeTransactionDataWare();
        }
    }

    /**
     * Returns the driver name of the driver layer of 'table'
     *
     * @param table table which driver name is wanted
     *
     * @return String
     */
    public static String getDriver(DBDataSource table) {
        return ((DBDataSourceAdapter) table).getDriver().getName();
    }
}
