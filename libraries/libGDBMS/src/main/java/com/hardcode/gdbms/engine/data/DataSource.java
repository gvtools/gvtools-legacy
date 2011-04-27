package com.hardcode.gdbms.engine.data;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.driver.ReadAccess;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;

import java.io.IOException;


/**
 * Interfaz que define los origenes de datos para operaciones internas del
 * motor de base de datos
 *
 * @author Fernando González Cortés
 */
public interface DataSource extends ReadAccess {
    /**
     * Avisa al driver de que van a realizarse operaciones sobre él. El driver
     * deberá de abrir el fichero, conectar a la base de datos
     * @throws ReadDriverException TODO
     */
    void start() throws ReadDriverException;

    /**
     * Cierra el DataSource. Todo acceso que no se sitúe temporalmente entre un
     * start y un stop tendrá un comportamiento indeterminado
     * @throws ReadDriverException TODO
     */
    void stop() throws ReadDriverException;

    /**
     * Devuelve el nombre del DataSource
     *
     * @return nombre de la tabla
     */
    String getName();

    /**
     * Devuelve el filtro que resultó de la cláusula where de la instrucción
     * que dió como resultado este DataSource.
     *
     * @return Filtro de la cláusula where o null si el DataSource no es
     *         resultado de una instrucción con cláusula where
     *
     * @throws IOException Si se produce un error accediendo a las estructuras
     *         de datos internas
     *
     * @deprecated
     */
    long[] getWhereFilter() throws IOException;

    /**
     * gets a reference to the factory object that created the DataSource
     *
     * @return DataSourceFactory
     */
    DataSourceFactory getDataSourceFactory();

    /**
     * Gets a memento object with the current status of the DataSource
     *
     * @return DataSourceMemento
     *
     * @throws MementoException If the state cannot be obtained
     */
    Memento getMemento() throws MementoException;

    /**
     * Sets the DataSourceFactory that created the DataSource instance
     *
     * @param dsf DataSourceFactory
     */
    public void setDataSourceFactory(DataSourceFactory dsf);

    /**
     * Sets the source information of the DataSource
     *
     * @param sourceInfo instance with the info
     */
    public void setSourceInfo(SourceInfo sourceInfo);

    /**
     * Gets the source information of the DataSource
     *
     * @return instance with the info
     */
    public SourceInfo getSourceInfo();

    /**
     * Gets the string representation of this DataSource
     *
     * @return String
     * @throws ReadDriverException TODO
     */
    public String getAsString() throws ReadDriverException;

    /**
     * Removes from the system the data source this DataSource instance
     * represents. No method can be called and no DataSource instance can be
     * obtained from the system after calling this method.
     * @throws WriteDriverException TODO
     */
    public void remove() throws WriteDriverException;

    /**
     * Gets the field ids of the DataSource primary keys fields
     *
     * @return int[] or null if the DataSource is not editable
     * @throws ReadDriverException TODO
     */
    public int[] getPrimaryKeys() throws ReadDriverException;

    /**
     * Gets the value of the primary key
     *
     * @param rowIndex row
     *
     * @return ArrayValue with the values of the primary key fields
     * @throws ReadDriverException TODO
     */
    public abstract ValueCollection getPKValue(long rowIndex)
        throws ReadDriverException;

    /**
     * Gets the name of the fieldIdth primary key field
     *
     * @param fieldId índice del campo cuyo nombre se quiere obtener
     *
     * @return String
     * @throws ReadDriverException TODO
     */
    public abstract String getPKName(int fieldId) throws ReadDriverException;

    /**
     * Get's the names of the primary key fields
     *
     * @return String[]
     * @throws ReadDriverException TODO
     */
    public abstract String[] getPKNames() throws ReadDriverException;

    /**
     * Returns the ith field type. Must be a java.sql.Types constant
     *
     * @param i índice del campo cuyo tipo se quiere conocer
     *
     * @return Class
     * @throws ReadDriverException TODO
     */
    public abstract int getPKType(int i) throws ReadDriverException;

    /**
     * Return the number of fields that are primary key of this DataSource
     *
     * @return
     * @throws ReadDriverException TODO
     */
    int getPKCardinality() throws ReadDriverException;

    /**
     * Gets the value of all fields at the specified row
     *
     * @param rowIndex index of the row to be retrieved
     *
     * @return Value[]
     * @throws ReadDriverException TODO
     */
    Value[] getRow(long rowIndex) throws ReadDriverException;

    /**
     * Gets the field names array
     *
     * @return String[]
     * @throws ReadDriverException TODO
     */
    String[] getFieldNames() throws ReadDriverException;

    /**
     * Obtiene el indice de un campo a partir de su nombre o -1 si no existe un
     * campo con ese nombre
     *
     * @param fieldName Nombre del campo
     *
     * @return Indice del campo con el nombre dado o -1 si el campo no existe
     * @throws ReadDriverException TODO
     */
    int getFieldIndexByName(String fieldName) throws ReadDriverException;

    /**
     * Gets a DataWare over the same data source of this DataSource
     *
     * @param mode One of DataSourceFactory.DATA_WARE_DIRECT_MODE,
     * DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER
     *
     * @return DataWare
     * @throws ReadDriverException TODO
     */
    DataWare getDataWare(int mode) throws ReadDriverException;
    
    /**
     * @throws ReadDriverException TODO
     * 
     */
    boolean isVirtualField(int fieldId) throws ReadDriverException;
    
    /**
     * 
     */
    Driver getDriver();
    
    
    /**
     * Fuerza el cierre y la apertura del DataSource
     * @throws ReloadDriverException TODO
     */
    public void reload() throws ReloadDriverException;

    /**
     * Regi
     */
    public void addDataSourceListener(IDataSourceListener listener);
    
    public void removeDataSourceListener(IDataSourceListener listener);

}
