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

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.gvsig.normalization.patterns.Element;
import org.gvsig.normalization.patterns.NormalizationPattern;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.parser.ParseException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

/**
 * Table normalization
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 */
public class TableNormalization extends AbstractNormalization {

	private static final Logger log = Logger
			.getLogger(TableNormalization.class);

	protected IEditableSource editableSourceTable = null;
	protected int indexSelectedField;
	protected String nameSelectedField;
	protected FieldDescription[] fieldDescTable = null;

	/**
	 * Constructor
	 * 
	 * @param source
	 * @param fieldIndex
	 *            selected field index
	 * @param pattern
	 *            pattern
	 */
	public TableNormalization(IEditableSource source, int fieldIndex,
			NormalizationPattern pattern) {

		super(pattern);
		this.editableSourceTable = source;
		this.indexSelectedField = fieldIndex;

		try {
			this.fieldDescTable = this.editableSourceTable
					.getFieldsDescription();
			this.nameSelectedField = this.editableSourceTable.getRecordset()
					.getFieldName(this.indexSelectedField);
		} catch (Exception e) {
			log.error("Getting the recordset of the table", e);
		}
	}

	/**
	 * Get if you want generate a new table
	 * 
	 * @return
	 */
	public boolean createNewTable() {
		return false;
	}

	/**
	 * Tasks necessary before normalize
	 * 
	 * @return process ok
	 */
	public boolean preProcess() {
		update("INFO.normalizing");
		return this.generateFieldsOriginalTable();
	}

	/**
	 * Tasks necessary after normalize
	 * 
	 * @return process ok
	 */
	public boolean postProcess() {
		update("INFO.endnormalizing");
		return false;
	}

	/**
	 * Get the row count
	 * 
	 * @return number of rows
	 */
	public long getRowCount() {
		int count = -1;
		try {
			return this.editableSourceTable.getRowCount();
		} catch (Exception e) {
			log.error("Getting the row count of the source", e);
		}
		return count;
	}

	/**
	 * This method fills the address elements in one row in the news field of
	 * the original table.
	 * 
	 * @throws DriverIOException
	 * @throws IOException
	 * @throws ParseException
	 * @throws DriverException
	 * @throws ExpansionFileReadException
	 * @throws ReadDriverException
	 * @throws ValidateRowException
	 */
	public void fillRow(int row) throws DriverIOException, IOException,
			ParseException, DriverException, ExpansionFileReadException,
			ReadDriverException, ValidateRowException {
		Value[] vals = createValueArray();
		List<String> splitString = splitStrings(row);
		fillValues(vals, splitString, row);
		editRow(vals, row);

	}

	/**
	 * Edit the row
	 * 
	 * @param vals
	 * @param row
	 * @throws DriverIOException
	 * @throws IOException
	 * @throws ReadDriverException
	 * @throws ExpansionFileReadException
	 * @throws ValidateRowException
	 * @throws EditionException
	 */
	protected void editRow(Value[] vals, int row) throws DriverIOException,
			IOException, ExpansionFileReadException, ReadDriverException,
			ValidateRowException {

		IRowEdited rowVals = this.editableSourceTable.getRow(row);
		rowVals.setAttributes(vals);
		this.editableSourceTable.validateRow(rowVals.getLinkedRow(),
				EditionEvent.ALPHANUMERIC);
		int calculatedIndex = ((EditableAdapter) this.editableSourceTable)
				.getCalculatedIndex((long) row);
		this.editableSourceTable.doModifyRow(calculatedIndex,
				rowVals.getLinkedRow(), EditionEvent.ALPHANUMERIC);
	}

	/**
	 * This method fills the rows with new values
	 * 
	 * @param vals
	 * @param splitString
	 * @param row
	 * @throws ParseException
	 * @throws DriverIOException
	 * @throws IOException
	 * @throws DriverException
	 * @throws ReadDriverException
	 * @throws ExpansionFileReadException
	 */
	protected void fillValues(Value[] vals, List<String> splitString, int row)
			throws ParseException, DriverIOException, IOException,
			DriverException, ExpansionFileReadException, ReadDriverException {
		/* fill the vector with the original values */
		IRowEdited rowVals = this.editableSourceTable.getRow(row);
		for (int j = 0; j < vals.length; j++) {
			vals[j] = rowVals.getAttribute(j);
		}

		/* fill the vector with the new fields */
		int positionInRowValues;

		int[] fieldTypes = new int[vals.length];
		for (int i = 0; i < vals.length; i++) {
			fieldTypes[i] = this.editableSourceTable.getFieldsDescription()[i]
					.getFieldType();
		}

		for (int j = 0; j < this.nameNewFields.length; j++) {
			String campo = ((String) this.nameNewFields[j]).trim();
			SelectableDataSource sds = this.editableSourceTable.getRecordset();
			positionInRowValues = sds.getFieldIndexByName(campo);
			int tipoCampo = fieldTypes[positionInRowValues];
			String cadena = "";

			try {
				cadena = (String) splitString.get(this.posNameNewFields[j]);
			} catch (Exception e) {
				update("ERROR.errornullsubstring." + (row + 1));
				cadena = "";
			}
			cadena = cadena.trim();

			// create values
			vals[positionInRowValues] = createValue(row, tipoCampo, this.posNameNewFields[j], cadena);

		}
	}

	/**
	 * This method split strings
	 * 
	 * @param row
	 * @return
	 * @throws DriverIOException
	 * @throws IOException
	 */
	protected List<String> splitStrings(int row) throws DriverIOException,
			IOException {
		IRowEdited iRow = null;

		try {
			iRow = this.editableSourceTable.getRow(row);
		} catch (Exception e) {
			try {
				this.editableSourceTable.getRecordset().reload();
				iRow = this.editableSourceTable.getRow(row);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		Value val = iRow.getAttribute(this.indexSelectedField);
		String splitString = val.toString().trim();
		List<String> cha = this.nAlgorithm.splitChain(splitString);
		this.nAlgorithm.setRow(row);
		return this.nAlgorithm.filterSplitChains(cha);
	}

	/**
	 * This method creates a new value array
	 * 
	 * @return
	 */
	protected Value[] createValueArray() {
		return new Value[this.editableSourceTable.getFieldsDescription().length];
	}

	/**
	 * This method checks if the field name already exists in the original table
	 * 
	 * @param fid
	 * @return
	 */
	private boolean checkFieldName(FieldDescription fid) {
		boolean exist = false;
		String name = fid.getFieldName();

		FieldDescription[] fields = this.editableSourceTable
				.getFieldsDescription();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getFieldAlias().compareTo(name) == 0) {
				exist = true;
			}
		}
		return exist;
	}

	/**
	 * This method generates the news fields in the original table
	 * 
	 * @return
	 */
	private boolean generateFieldsOriginalTable() {

		update("INFO.errormodifyoritable");

		boolean isOkNewFields = false;
		if (this.editableSourceTable.getOriginalDriver() instanceof IWriteable) {
			IWriteable aux = (IWriteable) this.editableSourceTable
					.getOriginalDriver();
			IWriter writer = aux.getWriter();

			if (writer.canAlterTable()) {
				// Extract pattern elements
				try {
					Element[] address = this.pattern.getArrayElements();
					FieldDescription filde;
					boolean exist = false;

					for (int i = 0; i < address.length; i++) {
						filde = this.createFieldDescFromPattern(address[i]);
						exist = checkFieldName(filde);
						if (address[i].getImportfield()) {
							if (exist) {
								update("ERROR.errordoubledname");
								isOkNewFields = false;
								break;
							}

							((EditableAdapter) this.editableSourceTable)
									.addField(filde);
							isOkNewFields = true;
						}
					}

				} catch (Exception e) {
					update("ERROR.erroraddfieldstable");
					log.error("ERROR adding new fields to the main table", e);

					isOkNewFields = false;
				}
			} else {
				update("ERROR.errortabledontaltered");
				log.error("ERROR, table not altered");
				isOkNewFields = false;
			}
		}
		return isOkNewFields;
	}
}
