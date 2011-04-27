package com.hardcode.gdbms.engine.data;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;

/**
 * @author Fernando González Cortés
 */
public abstract class DataSourceCommonImpl implements DataSource{

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getRow(long)
	 */
	public Value[] getRow(long rowIndex) throws ReadDriverException {
		Value[] ret = new Value[getFieldCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = getFieldValue(rowIndex, i);
		}

		return ret;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getFieldNames()
	 */
	public String[] getFieldNames() throws ReadDriverException {
		String[] ret = new String[getFieldCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = getFieldName(i);
		}

		return ret;
	}

    /**
     * gets a string representation of this datasource
     *
     * @return String
     */
    public String getAsString() throws ReadDriverException {
    
        StringBuffer aux = new StringBuffer();
        int fc = getFieldCount();
        int rc = (int) getRowCount();
    
        for (int i = 0; i < fc; i++) {
            aux.append(getFieldName(i).toUpperCase()).append("\t");
        }
    
        for (int row = 0; row < rc; row++) {
            for (int j = 0; j < fc; j++) {
                aux.append(getFieldValue(row, j)).append("\t");
            }
        }
    
        return aux.toString();
    }

}
