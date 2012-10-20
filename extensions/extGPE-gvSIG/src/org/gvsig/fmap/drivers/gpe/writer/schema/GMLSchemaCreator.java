package org.gvsig.fmap.drivers.gpe.writer.schema;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;

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
 * $Id: GMLSchemaCreator.java 15628 2007-10-30 11:07:58Z jpiera $
 * $Log$
 * Revision 1.3  2006-07-24 07:36:40  jorpiell
 * Se han hecho un cambio en los nombres de los metodos para clarificar
 *
 * Revision 1.2  2006/07/21 08:56:59  jorpiell
 * Se ha modificado la versión del esquema
 *
 * Revision 1.1  2006/07/19 12:29:39  jorpiell
 * Añadido el driver de GML
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class GMLSchemaCreator {
	public final static String GML_SUBSTITUTION_GROUP = "gml:_Feature";
	public final static String GML_GEOMETRY = "the_geom";
	public final static String NAMESPACE = "cit";
	public final static String TARGET_NAMESPACE = "http://www.gvsig.com/cit";
	public final static String GML_VERSION = "2.1.2";

	private static String featureName = null;
	private static String featureType = null;

	private Writer writer = null;

	public GMLSchemaCreator(File m_File) throws IOException {
		super();
		writer = new FileWriter(m_File);
		featureName = m_File.getName().replaceAll("\\.xsd", "");
		featureType = featureName + "_Type";

	}

	public void createFile(LayerDefinition lyrDef) throws IOException {
		writer.write(getInitFile());
		writer.write(getInitComplexTypes());

		writer.write(getAttribute(GML_GEOMETRY,
				GMLTypesConversor.gvSIGToGMLTypes(lyrDef.getShapeType())));
		FieldDescription[] fieldDescription = lyrDef.getFieldsDesc();
		for (int i = 0; i < fieldDescription.length; i++) {
			if (!(fieldDescription[i].getFieldName().compareTo(
					GMLSchemaCreator.GML_GEOMETRY) == 0)) {
				writer.write(getAttribute(fieldDescription[i]));
			}
		}

		writer.write(getEndComplexTypes());
		writer.write(getEndFile());
	}

	/**
	 * Writes the schema file
	 * 
	 * @throws IOException
	 * 
	 */
	public void writeFile() throws IOException {
		writer.close();
	}

	/**
	 * Creates the schema header File
	 * 
	 * @return
	 */
	private String getInitFile() {
		StringBuffer string = new StringBuffer();
		string.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		string.append("<xs:schema targetNamespace=\"" + TARGET_NAMESPACE
				+ "\" ");
		string.append("xmlns:" + NAMESPACE + "=\"" + TARGET_NAMESPACE + "\" ");
		string.append("xmlns:gml=\"http://www.opengis.net/gml\" ");
		string.append("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		string.append("elementFormDefault=\"qualified\" ");
		string.append("attributeFormDefault=\"unqualified\" version=\""
				+ GML_VERSION + "\">");
		string.append("<xs:import namespace=\"http://www.opengis.net/gml\" ");
		string.append("schemaLocation=\"feature.xsd\"/");
		string.append(">");
		return string.toString();
	}

	/**
	 * Creates the GML File last line
	 * 
	 * @return
	 */
	private String getEndFile() {
		StringBuffer string = new StringBuffer();
		string.append("</xs:schema>");
		return string.toString();
	}

	/**
	 * Creates the complex types header
	 * 
	 * @return
	 */
	private String getInitComplexTypes() {
		StringBuffer string = new StringBuffer();
		string.append("<xs:complexType xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		string.append("name=\"" + getFeatureType() + "\">");
		string.append("<xs:complexContent>");
		string.append("<xs:extension base=\"gml:AbstractFeatureType\">");
		string.append("<xs:sequence>");
		return string.toString();
	}

	/**
	 * Creates the complex types end tag
	 * 
	 * @return
	 */
	private String getEndComplexTypes() {
		StringBuffer string = new StringBuffer();
		string.append("</xs:sequence>");
		string.append("</xs:extension>");
		string.append("</xs:complexContent>");
		string.append("</xs:complexType>");
		string.append("<xs:element name=\"" + getFeatureName() + "\" ");
		string.append("type=\"" + NAMESPACE + ":" + getFeatureType() + "\" ");
		string.append("substitutionGroup=\"" + GML_SUBSTITUTION_GROUP + "\"/>");
		return string.toString();
	}

	/**
	 * Creates the attribute description
	 * 
	 * @param fieldDescription
	 * @return
	 */
	private String getAttribute(FieldDescription fieldDescription) {
		return getAttribute(fieldDescription.getFieldName(),
				GMLTypesConversor.gvSIGToXlinkTypes(fieldDescription
						.getFieldType()));
	}

	/**
	 * Creates the attribute description
	 * 
	 * @param attributeName
	 *            Attribute name
	 * @param attributeType
	 *            Attribute Type
	 * @return
	 */
	private String getAttribute(String attributeName, String attributeType) {
		StringBuffer string = new StringBuffer();
		string.append("<xs:element name=\"" + attributeName + "\" ");
		string.append("minOccurs=\"0\" ");
		string.append("nillable=\"true\" ");
		string.append("type=\"" + attributeType + "\"/>");
		return string.toString();
	}

	/**
	 * @return Returns the featureName.
	 */
	public static String getFeatureName() {
		return featureName;
	}

	/**
	 * @return Returns the featureType.
	 */
	public static String getFeatureType() {
		return featureType;
	}

}
