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

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.DefaultRow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.TableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.dbf.DbfWriter;

/**
 * Normalization a List of Strings from file
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 */

public class StringListNormalization extends AbstractNormalization implements
		NormalizationNewTable {

	private static final Logger log = Logger
			.getLogger(StringListNormalization.class);

	private List<String> fileChains = null;
	private NormalizationTableDriver driver = null;
	private File ouputFile = null;

	/**
	 * Constructor
	 * 
	 * @param pat
	 * @param chains
	 * @param theFile
	 */
	public StringListNormalization(NormalizationPattern pat,
			List<String> chains, File theFile) {

		super(pat);
		this.ouputFile = theFile;
		this.fileChains = chains;
		FieldDescription[] fieldDescs = new FieldDescription[this
				.numberNewImportedFields()];

		// New table
		int posi = -1;

		// Pattern fields
		for (int j = 0; j < this.pattern.getElements().size(); j++) {
			if (((Element) this.pattern.getElements().get(j)).getImportfield()) {
				posi++;
				fieldDescs[posi] = this
						.createFieldDescFromPattern((Element) this.pattern
								.getElements().get(j));
			}
		}
		this.driver = new NormalizationTableDriver(fieldDescs);
	}

	/**
	 * From a file, a new table is created
	 * 
	 * @return
	 */
	public boolean createNewTable() {
		return true;
	}

	/**
	 * Fill all rows with values normalized
	 * 
	 * @param row
	 */
	public void fillRow(int row) {
		int posi = -1;

		/* Values new table */
		Value[] vals = new Value[this.nameNewFields.length];

		/* CUTTING AND FILTERING */
		List<String> chains = null;
		String cadena = "";
		int tipoCampo = 0;

		String splitableString = (String) this.fileChains.get(row);

		List<String> splittedChains = this.nAlgorithm
				.splitChain(splitableString);
		this.nAlgorithm.setRow(row);
		chains = this.nAlgorithm.filterSplitChains(splittedChains);

		/* CREATING VALUES */
		for (int j = 0; j < this.nameNewFields.length; j++) {
			posi++;
			try {
				cadena = (String) chains.get(this.posNameNewFields[j]);
			} catch (Exception e) {
				log.warn("Null chain replaced for white");
				update("ERROR.errornullsubstring." + (row + 1));
				cadena = "";
			}
			cadena = cadena.trim();

			tipoCampo = this.driver.getFieldType(posi);
			// create values
			// vals[posi] = createValue(row, tipoCampo, posi, cadena);
			vals[posi] = createValue(row, tipoCampo, this.posNameNewFields[j],
					cadena);
		}
		this.driver.addRow(vals);
	}

	/**
	 * Get the row count
	 * 
	 * @return number of rows
	 */
	public long getRowCount() {
		try {
			return this.driver.getRowCount();
		} catch (Exception e) {
			log.error("Error retrieving the number of rows of the driver");
			return -1;
		}
	}

	/**
	 * Get the estimated row count
	 * 
	 * @return number of rows
	 */
	public long getEstimatedRowCount() {
		return this.fileChains.size();
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
	 * Tasks necessary after normalize. Write dbf file
	 * 
	 * @return process ok
	 */
	public boolean postProcess() {

		update("INFO.writtingdbf");
		DbfWriter dbf = new DbfWriter();
		dbf.setFile(this.getOuputFile());
		dbf.setCharset(Charset.defaultCharset());
		TableDefinition td = new TableDefinition();
		td.setName("secondTable");
		td.setFieldsDesc(this.driver.getFieldDescriptions());

		try {
			dbf.initialize(td);
			dbf.preProcess();
		} catch (Exception e) {
			log.error("ERROR initializing dbf", e);
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
	 * Get the driver
	 * 
	 * @return driver
	 */
	public NormalizationTableDriver getDriver() {
		return this.driver;
	}

	/**
	 * Get the output file
	 * 
	 * @return dbf file
	 */
	public File getOuputFile() {
		return this.ouputFile;
	}

}