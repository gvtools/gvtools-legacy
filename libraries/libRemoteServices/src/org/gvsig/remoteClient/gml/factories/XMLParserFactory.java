package org.gvsig.remoteClient.gml.factories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.gvsig.remoteClient.gml.exceptions.GMLException;
import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.xmlpull.v1.XmlPullParserException;

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
/* CVS MESSAGES:
 *
 * $Id: XMLParserFactory.java 9729 2007-01-15 13:11:17Z csanchez $
 * $Log$
 * Revision 1.3  2007-01-15 13:11:00  csanchez
 * Sistema de Warnings y Excepciones adaptado a BasicException
 *
 * Revision 1.2  2006/12/22 11:25:44  csanchez
 * Nuevo parser GML 2.x para gml's sin esquema
 *
 * Revision 1.1  2006/08/10 12:00:49  jorpiell
 * Primer commit del driver de Gml
 *
 *
 */
/**
 * Factory to create parsers to parse xml documents with
 * different schemas.
 * 
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 * @author Carlos Sánchez Periñán (sanchez_carper@gva.es)
 * 
 */
public class XMLParserFactory {
	private String encoding = "UTF-8";
	
	/**
	 * Creates a new XML parser
	 * First find the correct encoding to read the file
	 * @param m_File
	 * File to parse
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public XMLSchemaParser createSchemaParser(File m_File) throws GMLException{
		FileReader reader = null;       
		try {
			reader = new FileReader(m_File);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new GMLException(m_File.getName(),e);
		}
		BufferedReader br = new BufferedReader(reader);
		char[] buffer = new char[100];
		try {
			br.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new GMLException(m_File.getName(),e);
		}
		StringBuffer st = new StringBuffer(new String(buffer));
		
		// We find the encoding at the begining of the file
		
		String searchText = "encoding=\"";
		int index = st.indexOf(searchText);

		// If it find the encoding, it takes the new encoding, else it takes the default encoding "UTF-8"
		if (index>-1) { 
			st.delete(0, index+searchText.length());
			encoding = st.substring(0, st.indexOf("\""));
		}
		
		// make GML parser with the good enconding
		XMLSchemaParser parser = new XMLSchemaParser();
		
		// setImput(file,encoding): it sets the encoding to read with the KXML parser		
		try {
			parser.setInput(new FileInputStream(m_File), encoding);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new GMLException(m_File.getName(),e);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			throw new GMLException(m_File.getName(),e);
		}
		return parser;
	}
}
