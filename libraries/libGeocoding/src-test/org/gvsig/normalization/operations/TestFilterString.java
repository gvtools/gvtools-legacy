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
 * 2008 Prodevelop S.L  vsanjaime   programador
 */

package org.gvsig.normalization.operations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.gvsig.normalization.patterns.NormalizationPattern;

import com.iver.cit.gvsig.fmap.drivers.dbf.DBFDriver;
import com.iver.cit.gvsig.fmap.layers.XMLException;

/**
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 * 
 */
public class TestFilterString extends TestCase {

	private static final Logger log = Logger.getLogger(TestFilterString.class);

	public void testFilterString() throws MarshalException,
			FileNotFoundException, UnsupportedEncodingException,
			ValidationException, XMLException {

		log.info("TestFilterString: start the test");

		ArrayList<String> chains = new ArrayList<String>();
		chains.add(",XXX;9393;33.25;337.22;1/1/8");

		File fPat = new File(
				"./src-test/org/gvsig/normalization/testdata/PATNORM_TEST.xml");

		assertNotNull(fPat);

		NormalizationPattern pat = parserPat(fPat);
		assertNotNull(pat);

		File outputFile = null;
		try {
			outputFile = File.createTempFile("filters", ".dbf");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Normalization
		StringListNormalization norm = new StringListNormalization(pat, chains,
				outputFile);

		norm.preProcess();
		for (int i = 0; i < chains.size(); i++) {
			try {
				norm.fillRow(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		norm.postProcess();

		DBFDriver test = new DBFDriver();
		int nFields = -1;
		long nRows = -1;
		String val00 = null;
		String val01 = null;
		String val02 = null;
		String val03 = null;
		String val04 = null;
		String val05 = null;

		try {
			test.open(outputFile);
			nFields = test.getFieldCount();
			nRows = test.getRowCount();
			val00 = test.getFieldValue(0, 0).toString().trim();
			val01 = test.getFieldValue(0, 1).toString().trim();
			val02 = test.getFieldValue(0, 2).toString().trim();
			val03 = test.getFieldValue(0, 3).toString().trim();
			val04 = test.getFieldValue(0, 4).toString().trim();
			val05 = test.getFieldValue(0, 5).toString().trim();

			test.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(nFields, 6);
		assertEquals(nRows, 1);

		assertEquals(val00, "");
		assertEquals(val01, "XXX");
		assertEquals(val02, "9393");
		assertEquals(val03, "0.0");
		assertEquals(val04, "337.22");
		assertEquals(val05, "01-ene-0008");

		log.info("TestFilterString: test finished");
	}

	private NormalizationPattern parserPat(File f) throws MarshalException,
			FileNotFoundException, UnsupportedEncodingException,
			ValidationException, XMLException {
		NormalizationPattern pat = new NormalizationPattern();
		pat.loadFromXML(f);
		return pat;
	}

}
