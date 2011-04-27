package com.hardcode.gdbms.engine.data.driver;

import java.io.File;

import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.file.FileDataWare;


/**
 * Interface to the files.
 */
public interface FileDriver extends ReadAccess, GDBMSDriver {
    /**
     * Método invocado al comienzo para abrir el fichero. A partir de la
     * invocación de este método todos las operaciones se harán sobre el File
     * que se pasa como parámetro
     *
     * @param file Fichero que se debe de abrir
     * @throws OpenDriverException TODO
     */
    void open(File file) throws OpenDriverException;

    /**
     * Cierra el Fichero sobre el que se estaba accediendo
     * @throws CloseDriverException TODO
     */
    void close() throws CloseDriverException;

    /**
     * devuelve true si el driver puede leer el fichero que se pasa como
     * parámetro, false en caso contrario
     *
     * @param f Fichero que se quiere comprobar
     *
     * @return DOCUMENT ME!
     */
    boolean fileAccepted(File f);

    /**
     * Writes the content in the DataWare to the last opened file (method open).
     * The data must be written to a buffer (memory or disk) and then copied
     * to the file because if the file is modified the dataWare data may be
     * corrupted. A temporary file can be obtained from the DataSourceFactory
     * passed to the setDataSourceFactory method to delegate clean tasks to the
     * system
     *
     * @param dataWare DataWare with the contents
     * @throws WriteDriverException TODO
     * @throws ReadDriverException
     */
    void writeFile(FileDataWare dataWare) throws WriteDriverException, ReadDriverException;

    /**
     * Creates a new file with the given field names and types
     *
     * @param path Path to the new file
     * @param fieldNames Names of the fields
     * @param fieldTypes Types of the fields
     * @throws ReadDriverException TODO
     */
    void createSource(String path, String[] fieldNames, int[] fieldTypes) throws ReadDriverException;
}
