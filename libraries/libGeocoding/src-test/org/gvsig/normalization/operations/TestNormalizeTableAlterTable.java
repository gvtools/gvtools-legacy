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
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

/**
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicente Sanjaime Calvet</a>
 * 
 */

public class TestNormalizeTableAlterTable extends TestCase {

	private static final Logger log = PluginServices.getLogger();
	NormalizationPattern pat = new NormalizationPattern();
	File dbfFile = new File(
			"src-test/org/gvsig/normalization/operations/testdata/pro.dbf");
	File patFile = new File(
			"src-test/org/gvsig/normalization/operations/testdata/normAlterTable.xml");

	EditableAdapter source = new EditableAdapter();
	DBFDriver driver = null;
	FileDataSource fiDatSource = null;


	public void setUp() {

		try {
			pat.loadFromXML(patFile);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			driver = new DBFDriver();
			driver.open(dbfFile);

			fiDatSource = FileDataSourceFactory.newInstance();
			fiDatSource.setDriver(driver);
			String path = dbfFile.getAbsolutePath();
			String na = dbfFile.getName();
			FileSourceInfo fsi = new FileSourceInfo();
			fsi.file = path;
			fsi.name = na;
			fiDatSource.setSourceInfo(fsi);

			SelectableDataSource selDaSource = new SelectableDataSource(
					fiDatSource);

			source.setOriginalDataSource(selDaSource);
			source.startEdition(EditionEvent.GRAPHIC);

		} catch (Exception e) {
			log.error("Reading the driver", e);
		}
	}

	public void testNormalizeTableAlterTable() {
		TableNormalization norm = new TableNormalization(source, 5, pat);
		norm.preProcess();
		try {
			for (int i = 0; i < source.getRowCount(); i++) {

				norm.fillRow(i);

			}
		} catch (Exception e) {
			log.error("Normalizing", e);
		}
		norm.postProcess();

		System.out.println("Fin");

		String val09 = null;
		String val06 = null;
		String val17 = null;
		String val27 = null;
		String val29 = null;
		String val210 = null;

		int nFields = 0;
		long nRows = 0;

		try {

			// int fields = driver.getFieldCount();
			// long rows = driver.getRowCount();

			SelectableDataSource sds = source.getRecordset();
			nFields = sds.getFieldCount();
			nRows = sds.getRowCount();

			val09 = sds.getFieldValue(0, 9).toString().trim();
			val06 = sds.getFieldValue(0, 6).toString().trim();
			val17 = sds.getFieldValue(1, 7).toString().trim();
			val27 = sds.getFieldValue(2, 7).toString().trim();
			val29 = sds.getFieldValue(2, 9).toString().trim();
			val210 = sds.getFieldValue(2, 10).toString().trim();

		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(nFields, 11);
		assertEquals(nRows, 3);

		assertEquals(val09, "35.5");
		assertEquals(val06, "23030");
		assertEquals(val17, "743690");
		assertEquals(val27, "700347");
		assertEquals(val29, "26.0");
		assertEquals(val210, "12/11/2006");

	}

	public void tearDown() {
		try {
			driver.close();

			log.info("TEST FINISHED");
		} catch (CloseDriverException e) {
			log.error("Clossing the driver");
			e.printStackTrace();
		}
	}

}
