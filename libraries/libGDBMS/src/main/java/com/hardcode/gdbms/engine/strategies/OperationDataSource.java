package com.hardcode.gdbms.engine.strategies;

import java.io.IOException;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceCommonImpl;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.IDataSourceListener;
import com.hardcode.gdbms.engine.data.SourceInfo;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.ValueCollection;


/**
 * operation layer DataSource base class
 *
 * @author Fernando González Cortés
 */
public abstract class OperationDataSource extends DataSourceCommonImpl implements DataSource {
	private DataSourceFactory dsf;
	private String sql;
	private String name;
	private SourceInfo sourceInfo = null;

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDBMS()
	 */
	public String getDBMS() {
		return null;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getWhereFilter()
	 */
	public long[] getWhereFilter() throws IOException {
		return null;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDataSourceFactory()
	 */
	public DataSourceFactory getDataSourceFactory() {
		return dsf;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.HasProperties#getProperty(java.lang.String)
	 */
	public String getProperty(String propertyName) {
		return null;
	}

	/**
	 * sets the sql query of this operation DataSource. It's needed by the
	 * getMemento method which contains basically the sql
	 *
	 * @param sql query
	 */
	public void setSQL(String sql) {
		this.sql = sql;
	}

	/**
	 * Gets the SQL string that created this DataSource
	 *
	 * @return String with the query
	 */
	public String getSQL() {
		return sql;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#setSourceInfo(com.hardcode.gdbms.engine.data.DataSourceFactory.DriverInfo)
	 */
	public void setSourceInfo(SourceInfo sourceInfo) {
		this.sourceInfo = sourceInfo;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getSourceInfo()
	 */
	public SourceInfo getSourceInfo() {
		if (sourceInfo ==null) {
			sourceInfo = new SourceInfo();
			sourceInfo.name = this.name;
			sourceInfo.driverName = "GDBMS Operation Driver";
		}
		return sourceInfo;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param tableAlias
	 */
	public void setName(String tableAlias) {
		name = tableAlias;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#remove()
	 */
	public void remove() throws WriteDriverException {
		dsf.remove(this);
	}

    /**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getPrimaryKeys()
	 */
    public int[] getPrimaryKeys() throws ReadDriverException {
        return null;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKValue(long)
     */
    public ValueCollection getPKValue(long rowIndex) throws ReadDriverException {
	    throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKName(int)
     */
    public String getPKName(int fieldId) throws ReadDriverException {
	    throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKType(int)
     */
    public int getPKType(int i) throws ReadDriverException {
	    throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKCardinality()
     */
    public int getPKCardinality() throws ReadDriverException {
	    throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }
    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKNames()
     */
    public String[] getPKNames() throws ReadDriverException {
	    throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }
    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKNames()
     */
    public DataWare getDataWare(int mode) throws ReadDriverException{
	    throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }
    
    public boolean isVirtualField(int fieldId) throws ReadDriverException  {
    	return false;
    }
    
    public Driver getDriver() {
    	return null;
    }
    
	public void reload() throws ReloadDriverException {		
		
	}

	public void addDataSourceListener(IDataSourceListener listener) {
		
	}

	public void removeDataSourceListener(IDataSourceListener listener) {
		
	}    
}
