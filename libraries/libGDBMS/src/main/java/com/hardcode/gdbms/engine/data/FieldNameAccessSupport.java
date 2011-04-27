package com.hardcode.gdbms.engine.data;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;

import java.util.HashMap;


/**
 * Implementación del soporte para acceso a campos por nombre
 *
 * @author Fernando González Cortés
 */
public class FieldNameAccessSupport {
    private HashMap nameIndex = null;
    private DataSource dataSource;

    /**
     * Creates a new FieldNameAccessSupport object.
     *
     * @param ds DataSource con los campos sobre los que se accede
     */
    public FieldNameAccessSupport(DataSource ds) {
        dataSource = ds;
    }

    /**
     * @throws ReadDriverException TODO
     * @see com.hardcode.gdbms.engine.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
     */
    public int getFieldIndexByName(String fieldName) throws ReadDriverException {
        Integer i = (Integer) getNameIndex().get(fieldName.toUpperCase());

        if (i == null) {
            return -1;
        }

        return i.intValue();
    }

    /**
     * Obtiene una referencia a la tabla de indices por nombre, creando dicha
     * tabla si es la primera vez que se accede
     *
     * @return tabla indice-nombre
     * @throws ReadDriverException TODO
     */
    private HashMap getNameIndex() throws ReadDriverException {
        if (nameIndex == null || (nameIndex.size() < dataSource.getFieldCount())) {
            //Este metodo se invocará con el DataSource abierto ya
            nameIndex = new HashMap();

            for (int i = 0; i < dataSource.getFieldCount(); i++) {
                nameIndex.put(dataSource.getFieldName(i).toUpperCase(), new Integer(i));
            }
        }

        return nameIndex;
    }
}
