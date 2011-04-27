package com.hardcode.gdbms.engine.data;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.BadFieldDriverException;
import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.internalExceptions.InternalException;
import com.hardcode.gdbms.engine.internalExceptions.InternalExceptionCatcher;
import com.hardcode.gdbms.engine.internalExceptions.InternalExceptionEvent;
import com.hardcode.gdbms.engine.internalExceptions.Task;
import com.hardcode.gdbms.engine.internalExceptions.Timer;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;


/**
 * Decorator over data sources in order to apply a automatic opening mode
 *
 * @author Fernando González Cortés
 */
public class AutomaticDataSource implements DataSource {
    private static Logger logger = Logger.getLogger(AutomaticDataSource.class.getName());
    private DataSource ds;
    private boolean opened = false;
    private long timeout;
    private Timer timer = new Timer();
    private long lastReset = 0;

    /**
     * Creates a new AutomaticDataSource.
     *
     * @param ds DataSource to decorate
     * @param timeout DataSource will close if there is no operation
     * int timeout milliseconds
     */
    public AutomaticDataSource(DataSource ds, long timeout) {
        this.ds = ds;
        this.timeout = timeout;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#start()
     */
    public void start() throws ReadDriverException {
        //ignored
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#stop()
     */
    public void stop() throws ReadDriverException {
        if (opened){
            try {
				close();
			} catch (CloseDriverException e) {
				throw new ReadDriverException(getName(),e);
			}
        }
    }

    /**
     * Opens the datasource only if its closed
     * @throws OpenDriverException TODO
     */
    private synchronized void open() throws OpenDriverException {
        if (opened) {
            /*
             * the resetTimer is a long time operation. Will
             * only call it after a while
             */
            if ((System.currentTimeMillis() - lastReset) > (DataSourceFactory.DEFAULT_DELAY / 2)) {
                //reset the timer
//                logger.info("timer reset");
               	timer.resetTimer();
                lastReset = System.currentTimeMillis();
            }
        } else {
            opened = true;

            // Se abre
            try {
				ds.start();
			} catch (ReadDriverException e1) {
				throw new OpenDriverException(getName(),e1);
			}
//            logger.info("timer start");

            // Se inicia el timer
            timer.schedule(new Task() {
                    /**
                     * @see com.hardcode.gdbms.engine.internalExceptions.Task#execute()
                     */
                    public void execute() {
                        try {
                            synchronized (AutomaticDataSource.this) {
                                if (opened){
                                    close();
                                }
                            }
                        } catch (CloseDriverException e) {
                            InternalExceptionCatcher.callExceptionRaised(new InternalExceptionEvent(
                                    AutomaticDataSource.this,
                                    new InternalException(
                                        "Could not automatically close the data source",
                                        e)));
                        }
                    }

                }, timeout);
        }
    }

    private void close() throws CloseDriverException {
        synchronized (this) {
            //Cerramos el data source
            opened = false;
            try {
				ds.stop();
			} catch (ReadDriverException e) {
				throw new CloseDriverException(getName(),e);
			}

//            logger.info("datasource closed");

            AutomaticDataSource.this.timer.cancelTimer();
            AutomaticDataSource.this.timer = new Timer();
        }
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getName()
     */
    public String getName() {
        return ds.getName();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getWhereFilter()
     */
    public long[] getWhereFilter() throws IOException {
        return ds.getWhereFilter();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getDataSourceFactory()
     */
    public DataSourceFactory getDataSourceFactory() {
        return ds.getDataSourceFactory();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getMemento()
     */
    public Memento getMemento() throws MementoException {
        return ds.getMemento();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
     */
    public void setDataSourceFactory(DataSourceFactory dsf) {
        ds.setDataSourceFactory(dsf);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#setSourceInfo(com.hardcode.gdbms.engine.data.driver.DriverInfo)
     */
    public void setSourceInfo(SourceInfo sourceInfo) {
        ds.setSourceInfo(sourceInfo);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getSourceInfo()
     */
    public SourceInfo getSourceInfo() {
        return ds.getSourceInfo();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
     */
    public int getFieldIndexByName(String fieldName) throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}

        return ds.getFieldIndexByName(fieldName);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
     *      int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
        throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new BadFieldDriverException(getName(),e,String.valueOf(fieldId));
		}

        return ds.getFieldValue(rowIndex, fieldId);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
     */
    public int getFieldCount() throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}

        return ds.getFieldCount();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
     */
    public String getFieldName(int fieldId) throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}

        return ds.getFieldName(fieldId);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
     */
    public long getRowCount() throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}

        return ds.getRowCount();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
     */
    public int getFieldType(int i) throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}

        return ds.getFieldType(i);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getAsString()
     */
    public String getAsString() throws ReadDriverException {
        return ds.getAsString();
    }

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#remove()
	 */
	public void remove() throws WriteDriverException {
		ds.remove();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
		return ds.getPrimaryKeys();
	}

	/**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKValue(long)
     */
    public ValueCollection getPKValue(long rowIndex) throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
        return ds.getPKValue(rowIndex);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKName(int)
     */
    public String getPKName(int fieldId) throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
        return ds.getPKName(fieldId);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKType(int)
     */
    public int getPKType(int i) throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
        return ds.getPKType(i);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKCardinality()
     */
    public int getPKCardinality() throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
        return ds.getPKCardinality();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getRow(long)
     */
    public Value[] getRow(long rowIndex) throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
        return ds.getRow(rowIndex);
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getFieldNames()
     */
    public String[] getFieldNames() throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
        return ds.getFieldNames();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getPKNames()
     */
    public String[] getPKNames() throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
        return ds.getPKNames();
    }

    /**
     * @see com.hardcode.gdbms.engine.data.DataSource#getDataWare()
     */
    public DataWare getDataWare(int mode) throws ReadDriverException {
        return ds.getDataWare(mode);
    }

	public int getFieldWidth(int i) throws ReadDriverException {
        try {
			open();
		} catch (OpenDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
        return ds.getFieldWidth(i);
	}

	public boolean isVirtualField(int fieldId) throws ReadDriverException {
		// TODO Auto-generated method stub
		return ds.isVirtualField(fieldId);
	}

	public Driver getDriver() {
		return ds.getDriver();
	}

	public void reload() throws ReloadDriverException {
		try {
			this.stop();
		} catch (ReadDriverException e) {
			throw new ReloadDriverException(getName(),e);
		}
		ds.reload();
	}

	public void addDataSourceListener(IDataSourceListener listener) {
		ds.addDataSourceListener(listener);

	}

	public void removeDataSourceListener(IDataSourceListener listener) {
		ds.removeDataSourceListener(listener);

	}
}
