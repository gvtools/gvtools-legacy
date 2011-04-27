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
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.gvsig.normalization.patterns.NormalizationPattern;

import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.engine.data.file.FileDataSource;
import com.hardcode.gdbms.engine.data.file.FileDataSourceFactory;
import com.hardcode.gdbms.engine.data.file.FileSourceInfo;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.dbf.DBFDriver;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

/**
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicente Sanjaime Calvet</a>
 * 
 */
public class TestNormalizeTableJoinTable extends TestCase {

	private static final Logger log = PluginServices.getLogger();
	NormalizationPattern pat = new NormalizationPattern();
	File dbfFile = new File(
			"src-test/org/gvsig/normalization/operations/testdata/pro.dbf");
	File patFile = new File(
			"src-test/org/gvsig/normalization/operations/testdata/normAlterTable.xml");
	File outputFile;
	EditableAdapter source = new EditableAdapter();
	String[] joinNames = { "PROVINCIA", "COUNT" };
	DBFDriver test = null;

	public void setUp() {

		try {
			pat.loadFromXML(patFile);
		} catch (Exception e) {
			log.error("Error parsing the xml pattern", e);
		}

		try {
			test = new DBFDriver();
			test.open(dbfFile);

			FileDataSource fildas = FileDataSourceFactory.newInstance();
			fildas.setDriver(test);
			String path = dbfFile.getAbsolutePath();
			String na = dbfFile.getName();
			FileSourceInfo fsi = new FileSourceInfo();
			fsi.file = path;
			fsi.name = na;
			fildas.setSourceInfo(fsi);

			SelectableDataSource sds = new SelectableDataSource(fildas);

			source.setOriginalDataSource(sds);

		} catch (Exception e) {
			log.error("Reading the driver", e);
		}

	}

	public void testNormalizeTableJoinTable() {

		try {
			outputFile = File.createTempFile("jointable", ".dbf");

			JoinedTableNormalization norm = new JoinedTableNormalization(
					source, 5, pat, joinNames, outputFile);
			norm.preProcess();
			try {
				for (int i = 0; i < source.getRowCount(); i++) {

					norm.fillRow(i);

				}
			} catch (Exception e) {
				log.error("Normalizing", e);
			}
			norm.postProcess();

			// asserts

			DBFDriver test = new DBFDriver();
			int nFields = -1;
			long nRows = -1;
			String val02 = null;
			String val03 = null;
			String val04 = null;
			String val05 = null;
			String val06 = null;
			String val12 = null;
			String val15 = null;
			String val26 = null;

			try {
				test.open(outputFile);
				nFields = test.getFieldCount();
				nRows = test.getRowCount();
				val02 = test.getFieldValue(0, 2).toString().trim();
				val03 = test.getFieldValue(0, 3).toString().trim();
				val04 = test.getFieldValue(0, 4).toString().trim();
				val05 = test.getFieldValue(0, 5).toString().trim();
				val06 = test.getFieldValue(0, 6).toString().trim();
				val12 = test.getFieldValue(1, 2).toString().trim();
				val15 = test.getFieldValue(1, 5).toString().trim();
				val26 = test.getFieldValue(2, 6).toString().trim();

			} catch (Exception e) {
				e.printStackTrace();
			}

			assertEquals(nFields, 7);
			assertEquals(nRows, 3);

			assertEquals(val02, "23030");
			assertEquals(val03, "697096");
			assertEquals(val04, "4364444");
			assertEquals(val05, "35.5");
			assertEquals(val06, "01/07/2007");

			assertEquals(val12, "23030");
			assertEquals(val15, "100.8");

			assertEquals(val26, "12/11/2006");

			outputFile.delete();

		} catch (IOException e1) {
			log.error("Creating the temp file", e1);
		}
	}

	public void tearDown() {
		try {
			test.close();
			log.info("TEST FINISHED");
		} catch (CloseDriverException e) {
			log.error("Clossing the driver", e);
		}
	}
}
