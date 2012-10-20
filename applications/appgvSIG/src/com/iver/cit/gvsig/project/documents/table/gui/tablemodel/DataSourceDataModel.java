package com.iver.cit.gvsig.project.documents.table.gui.tablemodel;

import java.awt.Component;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.DateValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.DefaultRow;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public class DataSourceDataModel extends AbstractTableModel {
	private static Logger logger = Logger.getLogger(DataSourceDataModel.class
			.getName());
	// private SelectableDataSource dataSource;
	ProjectTable pt;
	boolean hasAssociatedLayer = false;
	private IRowEdited lastRowEdited;
	private int lastNumRow = -1;

	/**
	 * Crea un nuevo DataSourceDataModel.
	 * 
	 * @param pt
	 *            DOCUMENT ME!
	 */
	public DataSourceDataModel(ProjectTable pt) {
		this.pt = pt;
		if (pt.getAssociatedTable() != null) {
			hasAssociatedLayer = true;
		}
		// try {
		// dataSource = pt.getModelo().getRecordset();
		// } catch (DriverLoadException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * Returns the name of the field.
	 * 
	 * @param col
	 *            index of field
	 * 
	 * @return Name of field
	 */
	public String getColumnName(int col) {
		// if (col==0)
		// return " ";
		// col--;
		if (col >= pt.getMapping().length) {
			return null;
		}
		int i = pt.getMapping()[col];
		return pt.getAliases()[i];
	}

	/**
	 * Returns the number of fields.
	 * 
	 * @return number of fields
	 */
	public int getColumnCount() {
		return pt.getMapping().length;
	}

	/**
	 * Returns number of rows.
	 * 
	 * @return number of rows.
	 */
	public int getRowCount() {
		try {
			IEditableSource des = pt.getModelo();
			return des.getRowCount();
		} catch (ReadDriverException e) {
			return 0;
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param row
	 *            DOCUMENT ME!
	 * @param col
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Object getValueAt(int row, int col) {
		IEditableSource ies = pt.getModelo();
		if (hasAssociatedLayer) {
			Object stoppingEditing = ((FLayer) pt.getAssociatedTable())
					.getProperty("stoppingEditing");

			if (stoppingEditing != null
					&& ((Boolean) (stoppingEditing)).booleanValue()) {
				return ValueFactory.createValue("");
			}
		}
		// if (col==0){
		// return new Integer(row);
		// }
		// col--;
		int numRow;
		long[] orderIndexes = pt.getOrderIndexes();
		if (orderIndexes != null && row < orderIndexes.length) {
			numRow = (int) orderIndexes[row];
		} else {
			numRow = row;
		}
		Object obj = null;
		try {
			if (hasAssociatedLayer)
				ies.getRecordset().start();
			IRowEdited rowEdited = null;
			if (lastNumRow == numRow) {
				rowEdited = lastRowEdited;
			} else {
				rowEdited = ies.getRow(numRow);
				lastRowEdited = rowEdited;
				lastNumRow = numRow;
			}
			obj = rowEdited.getAttribute(pt.getMapping()[col]);

			// CHEMA: Problema con las fechas:
			// Esto deberia solucionarse en el ValueFactory.createValueByType
			if (obj instanceof DateValue) {
				Date date = ((DateValue) obj).getValue();

				String str = "" + (date.getMonth() + 1) + "/" + date.getDate()
						+ "/" + (date.getYear() + 1900);
				obj = ValueFactory.createValue(str);

			}

			// if (hasAssociatedLayer)
			// pt.getModelo().getRecordset().stop();
			// return obj;
		} catch (ReadDriverException e1) {
			logger.error("Error reading data row= " + row + " column= " + col,
					e1);
			obj = ValueFactory.createValue("");
		}
		if (hasAssociatedLayer)
			try {
				ies.getRecordset().stop();
			} catch (ReadDriverException e1) {
				logger.error("Error reading data row= " + row + " column= "
						+ col, e1);
				obj = ValueFactory.createValue("");
			}
		return obj;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * @param columnIndex
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// if (columnIndex==0)
		// return false;
		try {
			if (pt.getModelo().getRecordset().getFieldType(columnIndex) == Types.STRUCT)
				return true;
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		return pt.getModelo().isEditing();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param aValue
	 *            DOCUMENT ME!
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * @param columnIndex
	 *            DOCUMENT ME!
	 * 
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// if (columnIndex==0)
		// throw new
		// UnsupportedOperationException("Row Number is a read-only column");
		// columnIndex--;
		Value v;
		int numRow;
		if (pt.getOrderIndexes() != null) {
			numRow = (int) pt.getOrderIndexes()[rowIndex];
		} else {
			numRow = rowIndex;
		}
		if (getValueAt(rowIndex, columnIndex) == null
				|| getValueAt(rowIndex, columnIndex).toString().equals(aValue))
			return;
		try {
			IEditableSource des = pt.getModelo();
			try {
				FieldDescription fieldDescription = des.getRecordset()
						.getFieldsDescription()[columnIndex];
				int type = des.getRecordset().getFieldType(columnIndex);
				v = ValueFactory.createValueByType(aValue.toString(),
						fieldDescription.getFieldType(),
						fieldDescription.getFieldLength(),
						fieldDescription.getFieldDecimalCount());
			} catch (ParseException e) {
				if (aValue.equals("")) {
					v = ValueFactory.createNullValue();
				} else {
					JOptionPane.showMessageDialog(
							(Component) PluginServices.getMainFrame(),
							"formato_del_campo_incorrecto");
					return;
				}
			}
			IRowEdited row = null;
			if (lastNumRow == numRow) {
				row = lastRowEdited;
			} else {
				row = des.getRow(numRow);

			}
			Value[] values = row.getAttributes();
			values[columnIndex] = v;

			IRow newRow = null;

			if (row.getLinkedRow() instanceof IFeature) {
				IGeometry geometry = ((DefaultFeature) row.getLinkedRow())
						.getGeometry();
				newRow = new DefaultFeature(geometry, values, row.getID());
			} else {
				newRow = new DefaultRow(values, row.getID());
			}

			des.modifyRow(numRow, newRow, "Editar valor",
					EditionEvent.ALPHANUMERIC);
		} catch (ReadDriverException e1) {
			throw new RuntimeException(e1);
		} catch (NumberFormatException e) {
			/*
			 * NotificationManager.addError(PluginServices.-getText(this,
			 * "numero_incorrecto")+
			 * "\n"+PluginServices.-getText(this,"fila")+" = "+rowIndex+
			 * "\n"+PluginServices.-getText(this,"columna")+
			 * " = "+columnIndex,e);
			 */
		} catch (ValidateRowException e) {
			throw new RuntimeException(e);
		}
	}
}
