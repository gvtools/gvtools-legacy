package com.hardcode.gdbms.driver.dbf_Fernando;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.BadFieldDriverException;
import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.hardcode.gdbms.engine.data.file.FileDataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class DBFDriver implements Driver, FileDriver {
    private File file;
    private DbaseFile dbf = new DbaseFile();
    private char[] fieldTypes;

    /**
     * @see com.hardcode.driverManager.Driver#getName()
     */
    public String getName() {
        return "gdbms dbf driver";
    }

    /**
     * @see com.hardcode.gdbms.engine.data.GDBMSDriver#open(java.io.File)
     */
    public void open(File file) throws OpenDriverException {
        this.file = file;
        try {
			dbf.open(file);
		    fieldTypes = new char[getFieldCount()];

            for (int i = 0; i < fieldTypes.length; i++) {
                fieldTypes[i] = dbf.getFieldType(i);
            }
        } catch (ReadDriverException e) {
			throw new OpenDriverException(getName(),e);
		}

        /*            memory = new Value[getRowCount()][getFieldCount()];
           for (int i = 0; i < getRowCount(); i++){
                   for (int j = 0; j < getFieldCount(); j++){
                           try {
                                       memory[i][j] = getFieldValueOff(i,j);
                               } catch (IOException e) {
                                       e.printStackTrace();
                               } catch (SemanticException e) {
                                       e.printStackTrace();
                               }
                   }
           }
         */
    }

    /**
     * @see com.hardcode.gdbms.engine.data.GDBMSDriver#close()
     */
    public void close() throws CloseDriverException {
        try {
			dbf.close();
		} catch (IOException e) {
			throw new CloseDriverException(getName(),e);
		}
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
     *      int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
        throws ReadDriverException {
        // Field Type (C  or M)
        char fieldType = fieldTypes[fieldId];

        if (fieldType == 'L') {
            return ValueFactory.createValue(dbf.getBooleanFieldValue(
                    (int) rowIndex, fieldId));

            /*                }else if (fieldType == 'N'){
               String strValue = dbf.getStringFieldValue(rowIndex, fieldId);
               long value = Long.parseLong(strValue);
               if ((value > Integer.MIN_VALUE) && (value < Integer.MAX_VALUE)){
                       return new IntValue((int) value);
               }else{
                       return new LongValue(value);
               }
             */
        } else if ((fieldType == 'F') || (fieldType == 'N')) {
            String strValue = dbf.getStringFieldValue((int) rowIndex, fieldId)
                                 .trim();

            if (strValue.length() == 0) {
                return null;
            }

            double value = Double.parseDouble(strValue);

            return ValueFactory.createValue(value);
        } else if (fieldType == 'C') {
            return ValueFactory.createValue(dbf.getStringFieldValue(
                    (int) rowIndex, fieldId).trim());
        } else if (fieldType == 'D') {
            String date = dbf.getStringFieldValue((int) rowIndex, fieldId).trim();

            if (date.length() == 0) {
                return null;
            }

            String year = date.substring(0, 4);
            String month = date.substring(4, 6);
            String day = date.substring(6, 8);
            Calendar c = Calendar.getInstance();
            c.set(Integer.parseInt(year), Integer.parseInt(month),
                Integer.parseInt(day), 0, 0, 0);
            c.set(Calendar.MILLISECOND, 0);

            return ValueFactory.createValue(c.getTime());
        } else {
            throw new BadFieldDriverException(getName(),null,String.valueOf(fieldType));
        }
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
     */
    public int getFieldCount() throws ReadDriverException {
        return dbf.getFieldCount();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
     */
    public String getFieldName(int fieldId) throws ReadDriverException {
        return dbf.getFieldName(fieldId);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
     */
    public long getRowCount() throws ReadDriverException {
        return dbf.getRecordCount();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.FileDriver#fileAccepted(java.io.File)
     */
    public boolean fileAccepted(File f) {
        return f.getAbsolutePath().toUpperCase().endsWith("DBF");
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
     */
    public int getFieldType(int i) throws ReadDriverException {
        char fieldType = fieldTypes[i];

        if (fieldType == 'L') {
            return Types.BOOLEAN;
        } else if ((fieldType == 'F') || (fieldType == 'N')) {
            return Types.DOUBLE;
        } else if (fieldType == 'C') {
            return Types.VARCHAR;
        } else if (fieldType == 'D') {
            return Types.DATE;
        } else {
        	throw new BadFieldDriverException(getName(),null,String.valueOf(fieldType));
        }
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.DriverCommons#getDriverProperties()
     */
    public HashMap getDriverProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.DriverCommons#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
     */
    public void setDataSourceFactory(DataSourceFactory dsf) {
        // TODO Auto-generated method stub
    }

    /**
     * @throws ReadDriverException
     * @see com.hardcode.gdbms.engine.data.driver.FileDriver#writeFile(com.hardcode.gdbms.engine.data.file.FileDataWare,
     *      java.io.File)
     */
    public void writeFile(FileDataWare dataWare)
        throws WriteDriverException, ReadDriverException {
        try {
            File temp = File.createTempFile("gdbms", ".dbf");
            FileOutputStream fos = new FileOutputStream(temp);
            DataOutputStream dos = new DataOutputStream(fos);

            //version
            dos.writeByte(0x33);

            //date
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yy");
            byte year = Byte.parseByte(sdf.format(d));
            sdf = new SimpleDateFormat("MM");
            byte month = Byte.parseByte(sdf.format(d));
            sdf = new SimpleDateFormat("dd");
            byte day = Byte.parseByte(sdf.format(d));
            dos.writeByte(year);
            dos.writeByte(month);
            dos.writeByte(day);

            //record count
            int rowCount = (int) dataWare.getRowCount();
            dos.write(rowCount);

            //header length
            dos.write(296 + dataWare.getFieldCount() * 32);

        } catch (IOException e) {
            throw new WriteDriverException(getName(),e);
        }
    }

    public void createSource(String path, String[] fieldNames, int[] fieldTypes) throws ReadDriverException {
        // TODO Auto-generated method stub

    }
	public int getFieldWidth(int i) throws ReadDriverException {
		return dbf.getFieldLength(i);
	}
}
