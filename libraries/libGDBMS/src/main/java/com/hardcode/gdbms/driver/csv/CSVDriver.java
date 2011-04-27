package com.hardcode.gdbms.driver.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.FileNotFoundDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.hardcode.gdbms.engine.data.file.FileDataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.hardcode.gdbms.engine.values.ValueWriter;


/**
 * Driver para ficheros csv, en el que la primera fila se toma como la que
 * define los nombres de los campos
 *
 * @author Fernando González Cortés
 */
public class CSVDriver implements Driver, FileDriver, ValueWriter {
    private BufferedReader reader;
    private ArrayList lineas;
    private ValueWriter vWriter = ValueWriter.internalValueWriter;
    private File file;

    /**
     * @see com.hardcode.gdbms.driver.Driver#getName()
     */
    public String getName() {
        return "csv";
    }

    /**
     * @see com.hardcode.gdbms.data.DataSource#getFieldName(int)
     */
    public String getFieldName(int fieldId) throws ReadDriverException {
        String[] campos = (String[]) lineas.get(0);

        return campos[fieldId];
    }

    /**
     * @see com.hardcode.gdbms.data.DataSource#getIntFieldValue(int, int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
        throws ReadDriverException {
        String[] campos = (String[]) lineas.get((int) (rowIndex + 1));

        if (campos[fieldId].equals("null")) {
            return null;
        }

        if (fieldId == 0) {
            Value value = ValueFactory.createValue(Integer.parseInt(
                        campos[fieldId]));

            return value;
        }else if (fieldId == 3) {
            return ValueFactory.createValue(Date.valueOf(campos[fieldId]));
        }else if (fieldId == 4) {
            return ValueFactory.createValue(Time.valueOf(campos[fieldId]));
        }else if (fieldId == 5) {
            return ValueFactory.createValue(Timestamp.valueOf(campos[fieldId]));
        }


        Value value = ValueFactory.createValue(campos[fieldId]);

        return value;
    }

    /**
     * @see com.hardcode.gdbms.data.DataSource#getFieldCount()
     */
    public int getFieldCount() throws ReadDriverException {
        String[] campos = (String[]) lineas.get(0);

        return campos.length;
    }

    /**
     * @see com.hardcode.gdbms.data.DataSource#open(java.io.File)
     */
    public void open(File file) throws OpenDriverException {
        this.file = file;
        try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			throw new FileNotFoundDriverException(getName(),e1,file.getAbsolutePath());
		}

        lineas = new ArrayList();

        String aux;

        try {
			while ((aux = reader.readLine()) != null) {
			    String[] campos = aux.split(";");
			    lineas.add(campos);
			}
		} catch (IOException e) {
			throw new OpenDriverException(getName(),e);
		}
    }

    /**
     * @see com.hardcode.gdbms.data.DataSource#close()
     */
    public void close() throws CloseDriverException {
        try {
			reader.close();
		} catch (IOException e) {
			throw new CloseDriverException(getName(),e);
		}
    }

    /**
     * @see com.hardcode.gdbms.data.DataSource#getRowCount()
     */
    public long getRowCount() throws ReadDriverException {
        return lineas.size() - 1;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.FileDriver#fileAccepted(java.io.File)
     */
    public boolean fileAccepted(File f) {
        return f.getAbsolutePath().toUpperCase().endsWith("CSV");
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
     */
    public int getFieldType(int i) throws ReadDriverException {
        switch (i){
        case 0: return Types.INTEGER;
        case 1:
        case 2: return Types.VARCHAR;
        case 3: return Types.DATE;
        case 4: return Types.TIME;
        case 5: return Types.TIMESTAMP;
        case 6: return Types.BIGINT;
        }

        throw new RuntimeException();
    }

    /**
     * @throws ReadDriverException
     * @see com.hardcode.gdbms.engine.data.driver.FileDriver#writeFile(com.hardcode.gdbms.engine.data.edition.DataWare,
     *      java.io.File)
     */
    public void writeFile(FileDataWare dataWare)
        throws WriteDriverException, ReadDriverException {
        PrintWriter out;

        try {
            out = new PrintWriter(new FileOutputStream(file));

            String fieldRow = dataWare.getFieldName(0);

            for (int i = 1; i < (dataWare.getFieldCount() - 1); i++) {
                fieldRow += (";" + dataWare.getFieldName(i));
            }

            out.println(fieldRow);

            for (int i = 0; i < dataWare.getRowCount(); i++) {
                String row = dataWare.getFieldValue(i, 0).getStringValue(this);

                for (int j = 1; j < (dataWare.getFieldCount() - 1); j++) {
                    row += (";" +
                    dataWare.getFieldValue(i, j).getStringValue(this));
                }

                out.println(row);
            }

            out.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundDriverException(getName(),e,file.getAbsolutePath());
        }
    }

    public void createSource(String path, String[] fieldNames, int[] fieldTypes) throws ReadDriverException {
        File file = new File(path);
        try {
			file.createNewFile();
		    PrintWriter out = new PrintWriter(new FileOutputStream(file));
		    out.println("id;nombre;apellido;fecha;tiempo;marcatiempo");
		    out.close();
        } catch (IOException e) {
			throw new ReadDriverException(getName(),e);
		}
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getNullStatementString() {
        return vWriter.getNullStatementString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(boolean b) {
        return vWriter.getStatementString(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param binary DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(byte[] binary) {
        return vWriter.getStatementString(binary);
    }

    /**
     * DOCUMENT ME!
     *
     * @param d DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(Date d) {
        return d.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param d DOCUMENT ME!
     * @param sqlType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(double d, int sqlType) {
        return vWriter.getStatementString(d, sqlType);
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     * @param sqlType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(int i, int sqlType) {
        return vWriter.getStatementString(i, sqlType);
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(long i) {
        return vWriter.getStatementString(i);
    }

    /**
     * DOCUMENT ME!
     *
     * @param str DOCUMENT ME!
     * @param sqlType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(String str, int sqlType) {
        return str;
    }

    /**
     * DOCUMENT ME!
     *
     * @param t DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(Time t) {
        return t.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param ts DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(Timestamp ts) {
        return ts.toString();
    }

    public void setDataSourceFactory(DataSourceFactory dsf) {
    }

	public int getFieldWidth(int i) throws ReadDriverException {
		return 30;
	}
}
