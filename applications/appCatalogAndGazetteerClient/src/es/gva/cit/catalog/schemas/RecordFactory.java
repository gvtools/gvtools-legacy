package es.gva.cit.catalog.schemas;

import java.net.URI;
import java.util.ArrayList;

import es.gva.cit.catalog.metadataxml.XMLNode;

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
 * $Id: RecordFactory.java 600 2007-09-19 11:30:05 +0000 (Wed, 19 Sep 2007) jpiera $
 * $Log$
 * Revision 1.1.2.1  2007/07/23 07:14:24  jorpiell
 * Catalog refactoring
 *
 *
 */
/**
 * This class creates a parsed record from a uri and a XMLNode.
 * 
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class RecordFactory {
	private static ArrayList records = null;

	static {
		records = new ArrayList();
		records.add(new GeonetworkISO19115Record());
		records.add(new DeegreeISO19115Record());
		records.add(new DublinCoreRecord());
		records.add(new IdecISO19115Record());
		records.add(new IdeeISO19115Record());
		records.add(new Iso19139Record());
		records.add(new LaitsGmuISO19115Record());
		records.add(new LaitsGmuServicesRecord());
		records.add(new LaitsGmuEbRIMRecord());
	}

	/**
	 * Adds a new record
	 * 
	 * @param record
	 *            New record to add
	 */
	public static void addRecord(Record record) {
		records.add(record);
	}

	/**
	 * Try to identify the XML format and return a record
	 * 
	 * @param uri
	 *            Server URI (used to retrieve the images)
	 * @param node
	 *            XML node
	 * @return
	 */
	public static Record createRecord(URI uri, XMLNode node) {
		for (int i = 0; i < records.size(); i++) {
			Record record = (Record) records.get(i);
			if (node != null) {
				if (record.accept(uri, node)) {
					Object[] values = { uri, node };
					Class[] types = { URI.class, XMLNode.class };
					try {
						return (Record) record.getClass().getConstructor(types)
								.newInstance(values);
					} catch (Exception e) {
						// It the instance can be created the
						// default record has to be returned
					}
				}
			}
		}
		return new UnknownRecord(uri, node);
	}
}
