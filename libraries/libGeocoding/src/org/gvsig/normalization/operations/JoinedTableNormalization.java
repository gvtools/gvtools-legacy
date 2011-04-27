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

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.log4j.Logger;
import org.gvsig.normalization.patterns.Element;
import org.gvsig.normalization.patterns.NormalizationPattern;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.DefaultRow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.TableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.dbf.DbfWriter;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

/**
 * Normalize registries of the field of table in a new Table with relate fields
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicente Sanjaime Calvet</a>
 * 
 */
public class JoinedTableNormalization extends TableNormalization implements
		NormalizationNewTable {

	private static final Logger log = Logger
			.getLogger(JoinedTableNormalization.class);

	protected String[] nameRelateFields;
	private NormalizationTableDriver driver;
	private FieldDescription[] fieldDescNewTable;
	private File file;

	/**
	 * Constructor for a join normalization type
	 * 
	 * @param source
	 * @param index
	 * @param pattern2
	 * @param relateNames
	 */
	public JoinedTableNormalization(IEditableSource source, int index,
			NormalizationPattern pattern2, String[] relateNames, File afile) {
		super(source, index, pattern2);

		this.file = afile;
		this.nameRelateFields = relateNames;
		this.fieldDescNewTable = createFieldDescNewTable();
		this.driver = new NormalizationTableDriver(this.fieldDescNewTable);
	}

	/**
	 * This method answers if this process generates a new Table
	 * 
	 * @return true
	 */
	public boolean createNewTable() {
		return true;
	}

	/**
	 * Tasks necessary before normalize
	 * 
	 * @return process ok
	 */

	public boolean preProcess() {
		update("INFO.normalizing");
		return true;
	}

	/**
	 * Tasks necessary after normalize
	 * 
	 * @return process ok
	 */

	public boolean postProcess() {
		DbfWriter dbf = new DbfWriter();
		dbf.setFile(this.file);
		dbf.setCharset(Charset.defaultCharset());
		TableDefinition td = new TableDefinition();
		td.setName("secondTable");
		td.setFieldsDesc(driver.getFieldDescriptions());
		try {
			dbf.initialize(td);
			dbf.preProcess();
		} catch (Exception e1) {
			log.error("ERROR initializing the dbf file", e1);
			update("ERROR.errorwrittingdbf");
			return false;
		}		

		DefaultRowEdited dre;
		for (int i = 0; i < getRowCount(); i++) {
			try {
				Value[] vals = this.driver.getValues(i);
				DefaultRow dr = new DefaultRow(vals);
				dre = new DefaultRowEdited(dr, IRowEdited.STATUS_ADDED, i);
				dbf.process(dre);
			} catch (Exception ioe) {
				log.warn("ERROR traversing driver", ioe);
			}
		}
		try {
			dbf.postProcess();
		} catch (Exception e) {
			log.error("ERROR in the postprocess", e);
			update("ERROR.errorwrittingdbf");
			return false;
		}

		update("INFO.endnormalizing");
		return true;
	}

	/**
	 * Get the new table driver
	 * 
	 * @return driver
	 */
	public NormalizationTableDriver getDriver() {
		return this.driver;
	}

	/**
	 * Get DBF file of the new table
	 * 
	 * @return file
	 */
	public File getOuputFile() {
		return this.file;
	}

	/**
	 * This method add the selected row new Values
	 * 
	 * @param values
	 * @param row
	 */
	protected void editRow(Value[] vals, int row) {
		this.driver.addRow(vals);
	}

	/**
	 * @throws ReadDriverException 
	 * 
	 */
	protected void fillValues(Value[] vals, List<String> splitString, int row)
			throws DriverException, ReadDriverException {

		int posi = -1;
		/* COPY THE MAIN TABLE JOIN ATTRIBUTES */
		if (this.nameRelateFields != null) {
			int positionInColumnMainTable;
			SelectableDataSource sds = this.editableSourceTable.getRecordset();
			// COPY MAIN ELEMENTS
			for (int k = 0; k < this.nameRelateFields.length; k++) {
				posi++;
				positionInColumnMainTable = sds
						.getFieldIndexByName((String) this.nameRelateFields[k]);
				vals[posi] = sds.getFieldValue(row, positionInColumnMainTable);
			}
		}
		/* COPY NEW VALUES */
		String cadena;
		int tipoCampo;
		for (int j = 0; j < nameNewFields.length; j++) {
			posi++;
			try {
				cadena = (String) splitString.get(posNameNewFields[j]);
			} catch (Exception e) {
				log.warn("Null chain replaced for white");
				update("ERROR.errornullsubstring." + (row + 1));
				cadena = "";
			}
			cadena = cadena.trim();
			tipoCampo = driver.getFieldType(posi);
			// create values
			vals[posi] = createValue(row, tipoCampo, posi, cadena);
		}
	}

	/**
	 * Create the array of values with the necessary size
	 * 
	 * @return
	 */
	protected Value[] createValueArray() {
		int nnv = nameNewFields.length;
		int nvals = nameRelateFields == null ? nnv : nnv
				+ nameRelateFields.length;
		Value[] vals = new Value[nvals];
		return vals;
	}

	/**
	 * Create a Filed description new table
	 * 
	 * @return
	 */
	private FieldDescription[] createFieldDescNewTable() {

		// number of fields
		int num = nameRelateFields != null ? nameRelateFields.length
				+ nameNewFields.length : nameNewFields.length;
		FieldDescription[] res = new FieldDescription[num];
		// Relates fields
		int posi = -1;
		int ind1;
		FieldDescription auxfd = null;
		if (nameRelateFields != null) {
			for (int i = 0; i < nameRelateFields.length; i++) {
				posi++;
				try {
					ind1 = editableSourceTable.getRecordset()
							.getFieldIndexByName(nameRelateFields[i]);
					auxfd = fieldDescTable[ind1];
					res[posi] = auxfd;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// Pattern fields
		for (int j = 0; j < this.pattern.getElements().size(); j++) {
			if (((Element) this.pattern.getElements().get(j)).getImportfield()) {
				posi++;
				auxfd = createFieldDescFromPattern((Element) this.pattern
						.getElements().get(j));
				res[posi] = auxfd;
			}
		}
		return res;
	}

}