package com.hardcode.gdbms.engine.data.driver;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 */
public interface SpatialDBDriver extends DBDriver, SpatialDriver{

    /**
     * Ejecuta la instruccion que se pasa como parámetro en el gestor de base
     * de datos en el que está conectado el driver. Los nombres de las tablas
     * de la instrucción están preparados para el sistema de gestión donde se
     * ejecutará la instrucción
     *
     * @param con Conexión con la cual se ha de obtener el ResultSet
     * @param sql Instrucción SQL a ejecutar
     * @param props Properties of the overlaying DataSource layer
     *
     * @throws SQLException Si se produce un error ejecutando la instrucción
     *         SQL public void openTable(Connection con, String table,
     *         HasProperties props) throws SQLException;
     */
    public void open(Connection con, String sql, String tableName, String geomFieldName)
        throws SQLException;

}
