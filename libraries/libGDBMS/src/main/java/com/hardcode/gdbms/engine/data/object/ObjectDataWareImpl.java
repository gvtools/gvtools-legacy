package com.hardcode.gdbms.engine.data.object;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.db.FakeTransactionSupport;
import com.hardcode.gdbms.engine.data.edition.EditionInfo;
import com.hardcode.gdbms.engine.data.edition.PKTable;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * 
 */
public class ObjectDataWareImpl extends ObjectDriverDataSourceAdapter implements ObjectDataWare{

    private FakeTransactionSupport ftSupport = new FakeTransactionSupport(this);
    
    public void start() throws ReadDriverException {
        throw new RuntimeException("Invoke beginTrans in a DataWare");
    }

    public void stop() throws ReadDriverException {
        throw new RuntimeException("Invoke commitTrans/rollBackTrans in a DataWare");
    }

    public void beginTrans() throws ReadDriverException {
        super.start();
        ftSupport.beginTrans();
    }
    public void commitTrans() throws ReadDriverException, WriteDriverException {
        objectDriver.write(this);
        super.stop();
        ftSupport.commitTrans();
    }
    public void deleteRow(long rowId) throws WriteDriverException, ReadDriverException {
        ftSupport.deleteRow(rowId);
    }
    public void insertEmptyRow(ValueCollection pk) throws WriteDriverException {
        ftSupport.insertEmptyRow(pk);
    }
    public void insertFilledRow(Value[] values) throws WriteDriverException, ReadDriverException {
        ftSupport.insertFilledRow(values);
    }
    public void rollBackTrans() throws ReadDriverException, WriteDriverException {
        super.stop();
        ftSupport.rollBackTrans();
    }
    public void setFieldValue(long row, int fieldId, Value value) throws WriteDriverException, ReadDriverException {
        ValueCollection pkValue = null;
        Value[] originalRow = null;
        if (row < super.getRowCount()){
            pkValue = getOriginalPKValue(row);
            originalRow = getOriginalRow(row);
        }
        ftSupport.setFieldValue(row, fieldId, value, pkValue,
                originalRow);
    }
    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
     *      int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
        throws ReadDriverException {
        //If there is no transaction
        if (!ftSupport.isTransaction()) {
            return super.getFieldValue(rowIndex, fieldId);
        }
    
        //get where's the row
        EditionInfo fip = ftSupport.getPKTable().getIndexLocation((int) rowIndex);
        int flag = fip.getFlag();
    
        //get the value
        if ((flag == PKTable.ADDED) || (flag == PKTable.MODIFIED)) {
            return ftSupport.getInternalBuffer().getFieldValue(fip.getIndex(), fieldId);
        } else {
            return super.getFieldValue(fip.getIndex(), fieldId);
        }
    }
    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
     */
    public long getRowCount() throws ReadDriverException {
        if (ftSupport.isTransaction()) {
            //If there is a transaction
            return ftSupport.getPKTable().getPKCount();
        } else {
            //If there's not
            return super.getRowCount();
        }
    }
    /**
     * Gets the value of the original row
     *
     * @param rowIndex index of the row to be retrieved
     *
     * @return Row values
     * @throws ReadDriverException TODO
     */
    private Value[] getOriginalRow(long rowIndex) throws ReadDriverException {
        Value[] ret = new Value[getFieldCount()];
    
        for (int i = 0; i < ret.length; i++) {
            ret[i] = super.getFieldValue(rowIndex, i);
        }
    
        return ret;
    }
    /**
     * Gets the primary key value in the original DataSource
     *
     * @param rowIndex index of the row to be read
     *
     * @return PK
     * @throws ReadDriverException TODO
     */
    private ValueCollection getOriginalPKValue(long rowIndex) throws ReadDriverException {
        int[] fieldsId = getPrimaryKeys();
        Value[] pks = new Value[fieldsId.length];
    
        for (int i = 0; i < pks.length; i++) {
            pks[i] = super.getFieldValue(rowIndex, fieldsId[i]);
        }
    
        return ValueFactory.createValue(pks);
    }

}
