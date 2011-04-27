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

package org.gvsig.normalization.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.XMLEntity;

/**
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 * 
 */
public class TestPersistence extends TestCase {

	/**
	 * test
	 * 
	 * @throws XMLException
	 */
	public void testParseLoadXML() throws IOException, XMLException {

		NormalizationPattern pat = new NormalizationPattern();
		pat.setPatternname("pruebaParseXML.xml");
		pat.setNofirstrows(0);

		List<Element> elems = new ArrayList<Element>();

		Element elem1 = new Element();
		elem1.setFieldname("campo1");

		Fieldtype tipo = new Fieldtype();
		Stringvalue strval = new Stringvalue();
		strval.setStringvaluewidth(50);
		tipo.setStringvalue(strval);
		elem1.setFieldtype(tipo);

		elem1.setFieldwidth(30);

		Fieldseparator fsep = new Fieldseparator();
		fsep.setSemicolonsep(true);
		fsep.setJoinsep(false);
		elem1.setFieldseparator(fsep);

		Infieldseparators infsep = new Infieldseparators();
		infsep.setThousandseparator(",");
		infsep.setDecimalseparator(".");
		infsep.setTextseparator("\"");
		elem1.setInfieldseparators(infsep);

		elem1.setImportfield(true);

		elems.add(elem1);

		Element elem2 = new Element();
		elem2.setFieldname("campo2");

		Fieldtype tipo2 = new Fieldtype();
		Stringvalue strval2 = new Stringvalue();
		strval2.setStringvaluewidth(50);
		tipo2.setStringvalue(strval2);
		elem2.setFieldtype(tipo2);

		elem2.setFieldwidth(30);

		Fieldseparator fsep2 = new Fieldseparator();
		fsep2.setSemicolonsep(true);
		fsep2.setJoinsep(false);
		elem2.setFieldseparator(fsep2);

		Infieldseparators infsep2 = new Infieldseparators();
		infsep2.setThousandseparator(",");
		infsep2.setDecimalseparator(".");
		infsep2.setTextseparator("\"");
		elem2.setInfieldseparators(infsep2);

		elem2.setImportfield(true);

		elems.add(elem2);

		pat.setElements(elems);

		XMLEntity xml = pat.getXMLEntity();
		assertNotNull(xml);

		// pat2
		NormalizationPattern pat2 = new NormalizationPattern();
		pat2.setXMLEntity(xml);

		assertEquals(0, pat2.getNofirstrows());

		assertEquals("pruebaParseXML.xml", pat2.getPatternname());
		assertEquals(0, pat2.getNofirstrows());

		Element elem0 = ((Element) pat.getElements().get(0));

		assertEquals("campo1", elem0.getFieldname());
		assertEquals(true, elem0.getImportfield());

	}

}
