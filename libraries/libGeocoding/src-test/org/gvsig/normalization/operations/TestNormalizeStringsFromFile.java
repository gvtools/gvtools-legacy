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
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.gvsig.normalization.patterns.NormalizationPattern;

import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.dbf.DBFDriver;

/**
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicente Sanjaime Calvet</a>
 */

public class TestNormalizeStringsFromFile extends TestCase {

	private static final Logger log = PluginServices.getLogger();
	NormalizationPattern pat = new NormalizationPattern();
	ArrayList<String> chains = new ArrayList<String>();
	File file;
	DBFDriver test = null;

	public void setUp() {

		File f = new File(
				"./src-test/org/gvsig/normalization/operations/testdata/normFile.xml");
		try {
			pat.loadFromXML(f);
		} catch (Exception e) {
			e.printStackTrace();
		}

		chains.add("TEST1;TEST2;TEST3");
		chains.add("TEST4;TEST5;TEST6");
		chains.add("TEST7;TEST8;TEST9");
	}

	public void testNormalizeStringsFromFile() {

		try {
			file = File.createTempFile("temp", ".dbf");

			StringListNormalization norm = new StringListNormalization(pat,
					chains, file);
			norm.preProcess();
			for (int i = 0; i < chains.size(); i++) {
				try {
					norm.fillRow(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			norm.postProcess();

			// Asserts
			test = new DBFDriver();
			int nFields = -1;
			long nRows = -1;
			String val00 = null;
			String val01 = null;
			String val10 = null;
			String val11 = null;
			try {
				test.open(file);
				nFields = test.getFieldCount();
				nRows = test.getRowCount();
				val00 = test.getFieldValue(0, 0).toString().trim();
				val01 = test.getFieldValue(0, 1).toString().trim();
				val10 = test.getFieldValue(1, 0).toString().trim();
				val11 = test.getFieldValue(1, 1).toString().trim();
				test.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			assertEquals(nFields, 2);
			assertEquals(nRows, 3);

			assertEquals(val00, "TEST1");
			assertEquals(val10, "TEST4");
			assertEquals(val01, "TEST2;TEST3");
			assertEquals(val11, "TEST5;TEST6");

			file.delete();

		} catch (IOException e1) {
			log.error("Creating the temp file", e1);
		}

	}

	public void tearDown() {
		try {
			test.close();
			log.info("TEST FINISHED");
		} catch (CloseDriverException e) {
			log.error("Clossing the driver");
			e.printStackTrace();
		}
	}

}
