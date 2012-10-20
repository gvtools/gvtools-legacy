package org.gvsig.gpe.gml.utils;

import javax.xml.namespace.QName;

import org.gvsig.gpe.GPEDefaults;
import org.gvsig.gpe.gml.GmlProperties;
import org.gvsig.gpe.utils.StringUtils;
import org.gvsig.gpe.xml.XmlProperties;

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
/* CVS MESSAGES:
 *
 * $Id: GMLUtilsParser.java 189 2007-11-21 12:45:56Z csanchez $
 * $Log$
 * Revision 1.8  2007/06/28 13:05:09  jorpiell
 * The Qname has been updated to the 1.5 JVM machine. The schema validation is made in the GPEWriterHandlerImplementor class
 *
 * Revision 1.7  2007/05/18 10:41:01  csanchez
 * Actualización libGPE-GML eliminación de clases inecesarias
 *
 * Revision 1.6  2007/05/16 13:00:48  csanchez
 * Actualización de libGPE-GML
 *
 * Revision 1.5  2007/05/16 09:29:12  jorpiell
 * The polygons has to be closed
 *
 * Revision 1.4  2007/05/15 10:14:45  jorpiell
 * The element and the feature is managed like a Stack
 *
 * Revision 1.3  2007/05/15 09:35:09  jorpiell
 * the tag names cant have blanc spaces
 *
 * Revision 1.2  2007/05/07 12:58:42  jorpiell
 * Add some methods to manage the multigeometries
 *
 * Revision 1.1  2007/02/28 11:48:31  csanchez
 * *** empty log message ***
 *
 * Revision 1.1  2007/02/20 10:53:20  jorpiell
 * Añadidos los proyectos de kml y gml antiguos
 *
 * Revision 1.3  2007/01/15 13:11:00  csanchez
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
 * @author Jorge Piera Llodr� (piera_jor@gva.es)
 * @author Carlos S�nchez Peri��n (sanchez_carper@gva.es)
 */
public class GMLUtilsParser {

	/**
	 * Remove the blanc symbol from a tag
	 * 
	 * @param tag
	 *            Tag name
	 * @return The tag without blancs
	 */
	public static String removeBlancSymbol(String tag) {
		if (tag == null) {
			return null;
		}
		String blancSpace = GPEDefaults
				.getStringProperty(XmlProperties.DEFAULT_BLANC_SPACE);
		if (blancSpace == null) {
			blancSpace = GMLTags.GML_DEAFULT_BLANC_SPACE;
		}
		// PROBLEM WITH COMPATIBILITY OF "replaceAll()" WITH IBM J9 JAVA
		// MICROEDITION
		return StringUtils.replaceAllString(tag, blancSpace, " ");
		// return tag.replaceAll(blancSpace," ");
	}

	/**
	 * Replace the blancs of a tag with the deafult blanc symbol
	 * 
	 * @param tag
	 * @return A tag with blancs
	 */
	public static String addBlancSymbol(String tag) {
		if (tag == null) {
			return null;
		}
		String blancSpace = GPEDefaults
				.getStringProperty(XmlProperties.DEFAULT_BLANC_SPACE);
		if (blancSpace == null) {
			blancSpace = GMLTags.GML_DEAFULT_BLANC_SPACE;
		}
		// PROBLEM WITH COMPATIBILITY OF "replaceAll()" WITH IBM J9 JAVA
		// MICROEDITION
		return StringUtils.replaceAllString(tag, " ", blancSpace);
		// return tag.replaceAll(" ",blancSpace);
	}

	/**
	 * @return a default feature collection name
	 */
	public static QName createDefaultFeatureCollection() {
		String namespace = GPEDefaults
				.getStringProperty(XmlProperties.DEFAULT_NAMESPACE_URI);
		String localName = GPEDefaults
				.getStringProperty(GmlProperties.DEFAULT_FEATURECOLLECTION);
		return new QName(namespace, localName);
	}

	/**
	 * @return a default feature name
	 */
	public static QName createDefaultFeature() {
		String namespace = GPEDefaults
				.getStringProperty(XmlProperties.DEFAULT_NAMESPACE_URI);
		String localName = GPEDefaults
				.getStringProperty(GmlProperties.DEFAULT_FEATURE);
		return new QName(namespace, localName);
	}
}
