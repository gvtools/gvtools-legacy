package com.hardcode.gdbms.engine.data.driver;


import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.edition.DataWare;



/**
 * Interfaz que define los métodos de acceso de lectura
 *
 * @author Fernando González Cortés
 */
public interface ObjectDriver extends GDBMSDriver, ReadAccess {
    /**
     * Gets the primary key field indexes
     *
     * @return int[]
     * @throws ReadDriverException TODO
     */
    public int[] getPrimaryKeys() throws ReadDriverException;

    /**
     * Writes the content in the DataWare to the specified file
     *
     * @param dataWare DataWare with the contents
     * @throws WriteDriverException TODO
     * @throws ReadDriverException
     */
    void write(DataWare dataWare) throws WriteDriverException, ReadDriverException;

    public void reload() throws ReloadDriverException;
}
