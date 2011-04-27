package com.hardcode.gdbms.engine.strategies;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.FieldNameAccessSupport;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.values.Value;

/**
 * @author Fernando González Cortés
 */
public class AggregateDataSource extends OperationDataSource implements DataSource {
    
    private Value[] values;
    private String[] names;
	private FieldNameAccessSupport fnaSupport = new FieldNameAccessSupport(this);

    /**
     * @param aggregateds
     */
    public AggregateDataSource(Value[] aggregateds) {
        this.values = aggregateds;
        names = new String[aggregateds.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = "expr" + i;
        }
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#start()
     */
    public void start() throws ReadDriverException {
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#stop()
     */
    public void stop() throws ReadDriverException {
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getMemento()
     */
    public Memento getMemento() throws MementoException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getFieldIndexByName(java.lang.String)
     */
    public int getFieldIndexByName(String fieldName) throws ReadDriverException {
        return fnaSupport.getFieldIndexByName(fieldName);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long, int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
            throws ReadDriverException {
        return values[fieldId];
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
     */
    public int getFieldCount() throws ReadDriverException {
        return values.length;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
     */
    public String getFieldName(int fieldId) throws ReadDriverException {
        return names[fieldId];
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
     */
    public long getRowCount() throws ReadDriverException {
        return 1;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
     */
    public int getFieldType(int i) throws ReadDriverException {
        return values[i].getSQLType();
    }

	public int getFieldWidth(int i) throws ReadDriverException {
		// TODO Auto-generated method stub
		return values[i].getWidth();
	}
}
