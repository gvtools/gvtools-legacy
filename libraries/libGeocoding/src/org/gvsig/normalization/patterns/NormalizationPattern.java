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

/**
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicente Sanjaime Calvet</a>
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.gvsig.normalization.persistence.GeocodingPersistence;
import org.gvsig.normalization.persistence.GeocodingTags;

import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.xml.XMLEncodingUtils;
import com.iver.utiles.xmlEntity.generate.XmlTag;

/**
 * Class Patternnormalization.
 * 
 * This class is the normalization pattern. This pattern has your name
 * (_patternname), your xml file path (_patternurl), the attribute
 * (_nofirstrows) that says the number of the rows in the text file that will
 * not be normalized and the list of elements (_elements) that make the pattern
 */
public class NormalizationPattern implements GeocodingPersistence {

	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(NormalizationPattern.class);

	/**
	 * Pattern name
	 */
	private String _patternname;

	/**
	 * number of file rows that they will not be normalized
	 */
	private int _nofirstrows;

	/**
	 * List of elements. Each element is one new field. One element has
	 * attributes that they define the new field and split strings process.
	 */
	private List<Element> _elements;

	/**
	 * Constructor
	 */
	public NormalizationPattern() {
		this._elements = new ArrayList<Element>();
	}

	/**
	 * Returns the value of field 'elements'.
	 * 
	 * @return the value of field 'elements'.
	 */

	public List<Element> getElements() {
		return this._elements;
	}

	/**
	 * Returns the value of field 'elements'.
	 * 
	 * @return the value of field 'elements'.
	 */
	public Element[] getArrayElements() {
		Element[] eles = new Element[this._elements.size()];
		for (int i = 0; i < this._elements.size(); i++) {
			eles[i] = (Element) this._elements.get(i);
		}
		return eles;
	}

	/**
	 * Returns the value of field 'nofirstrows'.
	 * 
	 * @return the value of field 'nofirstrows'.
	 */
	public int getNofirstrows() {
		return this._nofirstrows;
	}

	/**
	 * Returns the value of field 'patternname'.
	 * 
	 * @return the value of field 'patternname'.
	 */
	public java.lang.String getPatternname() {
		return this._patternname;
	}

	/**
	 * Sets the value of field 'elements'.
	 * 
	 * @param elements
	 *            the value of field 'elements'.
	 */

	public void setElements(List<Element> elements) {
		this._elements = elements;
	}

	/**
	 * Sets the value of field 'nofirstrows'.
	 * 
	 * @param nofirstrows
	 *            the value of field 'nofirstrows'.
	 */
	public void setNofirstrows(int nofirstrows) {
		this._nofirstrows = nofirstrows;
	}

	/**
	 * Sets the value of field 'patternname'.
	 * 
	 * @param patternname
	 *            the value of field 'patternname'.
	 */
	public void setPatternname(String patternname) {
		this._patternname = patternname;
	}

	/**
	 * Save the pattern to XML file
	 * 
	 * @param file
	 * @throws XMLException
	 * @throws IOException
	 * @throws ValidationException
	 * @throws MarshalException
	 */
	public void saveToXML(File file) throws XMLException, IOException,
			MarshalException, ValidationException {
		OutputStream fos = new FileOutputStream(file.getAbsolutePath());
		OutputStreamWriter writer = new OutputStreamWriter(fos,
				GeocodingTags.PROJECTENCODING);
		Marshaller m = new Marshaller(writer);
		m.setEncoding(GeocodingTags.PROJECTENCODING);

		XMLEntity xml = this.getXMLEntity();
		xml.putProperty("followHeaderEncoding", true);
		m.marshal(xml.getXmlTag());
	}

	/**
	 * Load the pattern from XML file
	 * 
	 * @param reader
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws ValidationException
	 * @throws MarshalException
	 * @throws XMLException
	 */
	public void loadFromXML(File file) throws FileNotFoundException,
			UnsupportedEncodingException, MarshalException,
			ValidationException, XMLException {

		InputStream inStr = new FileInputStream(file);
		String encoding = XMLEncodingUtils.getEncoding(inStr);
		InputStreamReader reader = new InputStreamReader(inStr, encoding);
		XmlTag tag = (XmlTag) XmlTag.unmarshal(reader);
		XMLEntity xml = new XMLEntity(tag);
		this.setXMLEntity(xml);
	}

	/**
	 * Get class name
	 */
	public String getClassName() {
		return this.getClass().getName();
	}

	/**
	 * Persist object
	 */
	public XMLEntity getXMLEntity() throws XMLException {
		XMLEntity xml = new XMLEntity();
		xml.setName(GeocodingTags.NPATTERN);
		xml.putProperty(GeocodingTags.PATTERNNAME, this._patternname);
		xml.putProperty(GeocodingTags.NOFIRSTROWS, this._nofirstrows);
		Iterator<Element> it = _elements.iterator();
		while (it.hasNext()) {
			Element el = it.next();
			xml.addChild(el.getXMLEntity());
		}
		return xml;
	}

	/**
	 * Load object
	 */
	public void setXMLEntity(XMLEntity xml) throws XMLException {
		this._patternname = xml.getStringProperty(GeocodingTags.PATTERNNAME);
		this._nofirstrows = xml.getIntProperty(GeocodingTags.NOFIRSTROWS);
		this._elements.clear();
		int children = xml.getChildrenCount();
		for (int i = 0; i < children; i++) {
			Element elem = new Element();
			elem.setXMLEntity(xml.getChild(i));
			this._elements.add(elem);
		}
	}

}
