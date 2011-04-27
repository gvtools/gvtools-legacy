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
* 2008 Iver T.I.  {{Task}}
*/
 
package org.gvsig.gpe.xml;

import java.util.Properties;

import org.gvsig.gpe.GPEDefaults;
import org.gvsig.gpe.IGPEProperties;

/**
 * This class contains the properties for the XML
 * parsers and writers. This class has been registered using
 * the SPI (Service Provider Interface) and the values of their
 * properties can be configured using {@link GPEDefaults}.
 */
public class XmlProperties implements IGPEProperties{
	public static Properties properties = null;
	
	public XmlProperties(){
		properties = new Properties();
		properties.put(DEFAULT_NAMESPACE_PREFIX, DEFAULT_NAMESPACE_PREFIX_VALUE);
		properties.put(DEFAULT_NAMESPACE_URI, DEFAULT_NAMESPACE_URI_VALUE);
		properties.put(XSD_SCHEMA_FILE, XSD_SCHEMA_FILE_VALUE);
		properties.put(XML_VERSION, XML_VERSION_VALUE);
		properties.put(XML_ENCODING, XML_ENCODING_VALUE);
		properties.put(DEFAULT_BLANC_SPACE, DEFAULT_BLANC_SPACE_VALUE);
		properties.put(XML_SCHEMA_VALIDATED, XML_SCHEMA_VALIDATED_VALUE);
	}
	
	/**
	 * XML Schema prefix that have to be used to generate the file for the
	 * formats based on XML.
	 * @see 
	 * <a href="http://www.w3.org/XML/Schema">XML Schema</a> 
	 */
	public static final String DEFAULT_NAMESPACE_PREFIX = "namespacePrefix";
	private static final String DEFAULT_NAMESPACE_PREFIX_VALUE = "cit";
	
	/**
	 * Default namespace of the files based on XML.
	 * @see 
	 * <a href="http://www.w3.org/XML/Schema">XML Schema</a> 
	 */
	public static final String DEFAULT_NAMESPACE_URI= "namespaceURI";
	private static final String DEFAULT_NAMESPACE_URI_VALUE=  "http://www.gvsig.org/cit";
	
	/**
	 * Place where the XML Schema is located.
	 * @see 
	 * <a href="http://www.w3.org/XML/Schema">XML Schema</a> 
	 */
	public static final String XSD_SCHEMA_FILE = "schemaName";
	private static final String XSD_SCHEMA_FILE_VALUE = "cit.xsd";
		
	/**
	 * XML number of version.
	 * @see 
	 * <a href=" http://www.w3.org/XML/">XML</a> 
	 */
	public static final String XML_VERSION = "xmlVersion";
	private static final String XML_VERSION_VALUE =  "1.0";
	
	/**
	 * Encoding of the generated XML files.
	 * @see 
	 * <a href=" http://www.w3.org/XML/">XML</a> 
	 */
	public static final String XML_ENCODING = "xmlEncoding";
	private static final String XML_ENCODING_VALUE = "UTF-8";
	
	/**
	 * Character to replace the blank spaces in the names that
	 * has to be added like a XML node. On the writing process
	 * all the blank spaces are replaced by this character and on
	 * the reading process this character is replaced by a blank
	 * space.
	 */
	public static final String DEFAULT_BLANC_SPACE = "defaultBlancSpace";
	private static final String DEFAULT_BLANC_SPACE_VALUE = "_";
	
	/**
	 * If the parser can download XML Schemas. 
	 */
	public static final String XML_SCHEMA_VALIDATED  = "xmlSchemaValidated";
	private static final Boolean XML_SCHEMA_VALIDATED_VALUE = new Boolean(true);
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gpe.IGPEProperties#getProperties()
	 */
	public Properties getProperties() {
		return properties;
	}

}

