/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 * 2008 Prodevelop S.L. main development
 */

package org.gvsig.normalization.operations;

import java.util.ArrayList;
import java.util.List;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;

/**
 * Normalization Driver New Table
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 */

public class NormalizationTableDriver implements ObjectDriver {

	private List<Value[]> data = null;
	@SuppressWarnings("unused")
	private DataSourceFactory factory = null;
	private FieldDescription[] fieldDescS = null;

	/**
	 * Builder
	 * 
	 * @param fds
	 *            fields description
	 */
	public NormalizationTableDriver(FieldDescription[] fds) {
		this.data = new ArrayList<Value[]>();
		this.fieldDescS = fds;
	}

	/**
	 * Add a new row into the driver
	 * 
	 * @param row
	 */
	public void addRow(Value[] row) {
		this.data.add(row);
	}

	/**
	 * Return the Driver PrimaryKeys
	 * 
	 * @return
	 */
	public int[] getPrimaryKeys() {
		int[] resp = { 0 };
		return resp;
	}

	/**
	 * Reload
	 * 
	 */
	public void reload() {

	}

	/**
	 * write
	 * 
	 * @param dataWare
	 */
	public void write(DataWare dataWare) {

	}

	/**
	 * Set data source factory
	 * 
	 * @param dsf
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.factory = dsf;
	}

	/**
	 * Name of driver
	 * 
	 * @return name
	 */
	public String getName() {
		return "normalization";
	}

	/**
	 * Number of fields
	 * 
	 * @return number of fields
	 */
	public int getFieldCount() {
		return this.fieldDescS.length;
	}

	/**
	 * Get the field name from index
	 * 
	 * @param id
	 * @return name
	 */
	public String getFieldName(int id) {
		return this.fieldDescS[id].getFieldName();
	}

	/**
	 * Get the field type
	 * 
	 * @param id
	 * @return type
	 */
	public int getFieldType(int id) {
		return this.fieldDescS[id].getFieldType();
	}

	/**
	 * Get the value
	 * 
	 * @param rowIndex
	 * @param fieldId
	 * @return Value
	 */
	public Value getFieldValue(long rowIndex, int fieldId) {
		Value[] aux = (Value[]) this.data.get((int) rowIndex);
		return aux[fieldId];
	}

	/**
	 * Get the field width
	 * 
	 * @param id
	 * @return width
	 */
	public int getFieldWidth(int id) {

		return this.fieldDescS[id].getFieldLength();
	}

	/**
	 * Get the number of rows
	 * 
	 * @return number of rows
	 */
	public long getRowCount() {
		return this.data.size();
	}

	/**
	 * Get the Values of the one row
	 * 
	 * @param id
	 * @return array with Values
	 */
	public Value[] getValues(int id) {
		Value[] values = new Value[this.fieldDescS.length];
		Value value;
		// try {
		for (int j = 0; j < this.getFieldCount(); j++) {
			value = this.getFieldValue(id, j);
			values[j] = value;
		}
		return values;
	}

	/**
	 * Get the field description
	 * 
	 * @return field description
	 */
	public FieldDescription[] getFieldDescriptions() {
		return this.fieldDescS;
	}

}
