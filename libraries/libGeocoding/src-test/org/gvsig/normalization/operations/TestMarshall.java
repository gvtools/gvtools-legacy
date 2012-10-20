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
 * 2008 Prodevelop S.L  main developer
 */

package org.gvsig.normalization.operations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.gvsig.normalization.patterns.Element;
import org.gvsig.normalization.patterns.NormalizationPattern;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.XMLException;

public class TestMarshall extends TestCase {

	private static final Logger log = PluginServices.getLogger();

	public void testMarshallUnmarshall() throws MarshalException,
			ValidationException, XMLException, IOException {

		log.info("testMarshallUnmarshall. start test");
		NormalizationPattern pat = new NormalizationPattern();
		NormalizationPattern pat3 = new NormalizationPattern();
		File file = new File(
				"src-test/org/gvsig/normalization/operations/testdata/patSplitChain.xml");

		// PARSER

		pat.loadFromXML(file);

		assertEquals(11, pat.getElements().size());
		assertEquals(0, pat.getNofirstrows());

		Element elem1 = (Element) pat.getElements().get(0);

		assertNotNull(elem1);
		assertEquals("NewField", elem1.getFieldname());

		// SERIALIZER
		File ftemp = File.createTempFile("temp", "txt");
		pat.saveToXML(ftemp);

		// PARSER

		assertNotNull(ftemp);

		pat3.loadFromXML(ftemp);

		Element elem2 = (Element) pat3.getElements().get(0);

		assertNotNull(elem2);

		assertEquals(elem1.getImportfield(), elem2.getImportfield());
		assertEquals(elem1.getFieldwidth(), elem2.getFieldwidth());
		assertEquals(elem1.getFieldname(), elem2.getFieldname());
		assertEquals(elem1.getInfieldseparators().getDecimalseparator(), elem2
				.getInfieldseparators().getDecimalseparator());

	}

	public void testUnmarshall() throws MarshalException,
			FileNotFoundException, UnsupportedEncodingException,
			ValidationException, XMLException {

		log.info("testUnmarshall. start test");
		File file = new File(
				"src-test/org/gvsig/normalization/operations/testdata/patSplitChain.xml");
		// Marshal the person object
		NormalizationPattern pat = new NormalizationPattern();
		pat.loadFromXML(file);

		log.info("pattern loaded");
		// results
		assertEquals(11, pat.getElements().size());
		assertEquals(0, ((Element) pat.getElements().get(3)).getFieldwidth());
		assertEquals(true,
				((Element) pat.getElements().get(2)).getImportfield());
		assertEquals(true, ((Element) pat.getElements().get(4))
				.getFieldseparator().getSemicolonsep());

		log.info("testMarshallUnmarshall. test finished");
	}

}
