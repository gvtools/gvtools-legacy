/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.gui;

import javax.swing.table.AbstractTableModel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.indexes.FixedIndexSet;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class IndexedGDBMSTableModel extends AbstractTableModel {
	private DataSource source;
	private FixedIndexSet fis;

	/**
	 * Crea un nuevo IndexedGDBMSTableModel.
	 *
	 * @param ds DOCUMENT ME!
	 * @param fis DOCUMENT ME!
	 */
	public IndexedGDBMSTableModel(DataSource ds, FixedIndexSet fis) {
		source = ds;
		this.fis = fis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param ds DOCUMENT ME!
	 */
	public void setDataSource(DataSource ds) {
		source = ds;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		try {
			return source.getFieldCount();
		} catch (ReadDriverException e) {
			return 0;
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		try {
			return (int) source.getRowCount();
		} catch (ReadDriverException e) {
			return 0;
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int arg0, int arg1) {
		try {
			return source.getFieldValue(fis.getIndex(arg0), arg1);
		} catch (Exception e) {
			e.printStackTrace();

			return e.getMessage();
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return Returns the source.
	 */
	public DataSource getDataSource() {
		return source;
	}
}
