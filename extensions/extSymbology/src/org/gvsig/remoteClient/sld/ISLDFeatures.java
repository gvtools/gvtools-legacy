/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package org.gvsig.remoteClient.sld;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
/**
 * Interface that has to be implemented by all the SLDClases.
 * 
 *
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 *
 */

public interface ISLDFeatures {

	/**
	 * Parses the contents of the SLD document to extract the 
	 * information about a specific SLD Feature
	 * 
	 * @param parser
	 * @param cuTag
	 * @param expressionType
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws LegendDriverException
	 */
	public void parse(XMLSchemaParser parser, int cuTag, String expressionType) throws XmlPullParserException, IOException, LegendDriverException;
	/**
	 * Translate the object into an XML String according with
	 * the Styled Layer Specification
	 * 
	 * @return
	 */
	public String toXML();
	
}
