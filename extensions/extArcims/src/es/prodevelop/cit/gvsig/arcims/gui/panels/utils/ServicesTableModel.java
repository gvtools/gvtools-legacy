/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.gui.panels.utils;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;

import com.iver.andami.PluginServices;

/**
 * This is a subclass of the JTable's data model. It simply prevents cells from
 * being edited.
 * 
 * @see javax.swing.table.DefaultTableModel
 * 
 * @author jldominguez
 */
public class ServicesTableModel extends DefaultTableModel {
	private static Logger logger = Logger.getLogger(ServicesTableModel.class
			.getName());
	private static final long serialVersionUID = 0;

	public ServicesTableModel(Vector data, Vector cols) {
		super(data, cols);
	}

	/**
	 * Sets to <b>false</b> the possibility to edit any cell.
	 * 
	 * @return <b>false</b> (always)
	 */
	public boolean isCellEditable(int x, int y) {
		return false;
	}

	/**
	 * Utility method to leave <tt>n</tt> significant digits in a double number.
	 * 
	 * @param d
	 *            the original number
	 * @param n
	 *            the number of significant digits desired
	 * @return the number with n significant digits
	 */
	public static String leaveNDigits(double d, int n) {
		if (d == 0.0) {
			return "0.0";
		}

		long integ = Math.round(d);

		if ((d - integ) == 0) {
			return Double.toString(d);
		}

		long digitsBeforePoint = Math.round(Math.floor(1.0 + (Math.log(Math
				.abs(d)) / Math.log(10.0))));

		// if (d < 0) sigDigits++;
		if (digitsBeforePoint >= n) {
			logger.warn("Unable to round double: " + Double.toString(d));

			return Double.toString(d);
		}

		double factor = Math.pow(10.0, 1.0 * (n - digitsBeforePoint));
		double newd = d * factor;
		integ = Math.round(Math.floor(newd));
		newd = (1.0 * integ) / factor;

		return Double.toString(newd);
	}

	/**
	 * Gets the value stored in a row under a certain column name.
	 * 
	 * @param t
	 *            the JTable
	 * @param colname
	 *            the column name
	 * @param row
	 *            the row index
	 * @return the value stored in the row under that column name
	 */
	public static String getColumnValueOfRow(JTable t, String colname, int row) {
		for (int i = 0; i < t.getColumnCount(); i++) {
			if ((t.getColumnName(i).compareToIgnoreCase(colname)) == 0) {
				return (String) t.getValueAt(row, i);
			}
		}

		return "Not found";
	}

	/**
	 * Gets the value stored in a row under a certain column index.
	 * 
	 * @param t
	 *            the JTable
	 * @param colind
	 *            the column index
	 * @param row
	 *            the row index
	 * @return the value stored in the row under that column name
	 */
	public static String getColumnValueOfRowWithIndex(JTable t, int colind,
			int row) {
		return (String) t.getValueAt(row, colind);

		// for (int i=0; i<t.getColumnCount(); i++) {
		// if ((t.getColumnName(i).compareToIgnoreCase(colname)) == 0) {
		// return (String) t.getValueAt(row, i);
		// }
		// }
		// return "Not found";
	}

	public static int getColumnIndex(JTable t, String colName) {
		int col_ind = -1;

		for (int i = 0; i < t.getColumnCount(); i++) {
			if (t.getColumnName(i).compareToIgnoreCase(colName) == 0) {
				col_ind = i;

				break;
			}
		}

		return col_ind;
	}

	/**
	 * Finds out the index of the first row containing a certain string under a
	 * certain column name.
	 * 
	 * @param t
	 *            the table
	 * @param colName
	 *            the column name
	 * @param val
	 *            the value to be searched for
	 * @return the index of the row that contains the value
	 * @throws ArcImsException
	 */
	public static int getFirstRowWithValueInColumnName(JTable t,
			String colName, String val) throws ArcImsException {
		ArcImsException aie;

		int col_ind = getColumnIndex(t, colName);

		if (col_ind == -1) {
			aie = new ArcImsException(PluginServices.getText(null,
					"column_not_found") + ": " + colName);
			logger.error("Column not found. ", aie);
			throw aie;
		}

		for (int i = 0; i < t.getRowCount(); i++) {
			if (((String) t.getValueAt(i, col_ind)).compareToIgnoreCase(val) == 0) {
				return i;
			}
		}

		aie = new ArcImsException(PluginServices.getText(null,
				"value_not_found") + ": " + val);
		logger.error("Value not found in that column. ", aie);
		throw aie;
	}

	/**
	 * Finds out the index of the first row containing a certain string under a
	 * certain column index.
	 * 
	 * @param t
	 *            the table
	 * @param colIndex
	 *            the column index
	 * @param val
	 *            the value to be searched for
	 * @return the index of the row that contains the value
	 * @throws ArcImsException
	 */
	public static int getFirstRowWithValueInColumnIndex(JTable t, int colIndex,
			String val) throws ArcImsException {
		ArcImsException aie;

		if ((colIndex < 0) || (colIndex >= t.getColumnCount())) {
			aie = new ArcImsException(PluginServices.getText(null,
					"column_not_found") + ": " + colIndex);
			logger.error("Column not found. ", aie);
			throw aie;
		}

		for (int i = 0; i < t.getRowCount(); i++) {
			if (((String) t.getValueAt(i, colIndex)).compareToIgnoreCase(val) == 0) {
				return i;
			}
		}

		aie = new ArcImsException(PluginServices.getText(null,
				"value_not_found") + ": " + val);
		logger.error("Value not found in that column. ", aie);
		throw aie;
	}

	public void moveRow(int start, int end, int to) {
	}
}
