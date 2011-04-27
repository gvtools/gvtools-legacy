package org.gvsig.fmap.drivers.gpe.writer.schema;

import java.awt.Point;
import java.awt.Polygon;
import java.sql.Types;

import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;

import com.hardcode.gdbms.engine.spatial.Geometry;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.vividsolutions.jts.geom.GeometryCollection;

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
 * $Id: GMLTypesConversor.java 7717 2006-10-02 08:41:23Z jorpiell $
 * $Log$
 * Revision 1.6  2006-10-02 08:41:23  jorpiell
 * Actualizados los drivers de GML
 *
 * Revision 1.4.4.1  2006/09/19 12:22:48  jorpiell
 * Ya no se depende de geotools
 *
 * Revision 1.5  2006/09/18 12:09:43  jorpiell
 * El driver de GML ya no depende de geotools
 *
 * Revision 1.4  2006/07/24 08:28:09  jorpiell
 * Añadidos algunos tipos de datos en la conversión de java a gvSIG
 *
 * Revision 1.3  2006/07/24 07:36:40  jorpiell
 * Se han hecho un cambio en los nombres de los metodos para clarificar
 *
 * Revision 1.2  2006/07/21 08:57:28  jorpiell
 * Se ha añadido la exportación de capas de puntos
 *
 * Revision 1.1  2006/07/19 12:29:39  jorpiell
 * Añadido el driver de GML
 *
 *
 */
/**
 * Types conversor from GML to gvSIG
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class GMLTypesConversor {

	 /**
	  * Make a conversion between the gvSIG LayerDefinition and a geotools
	  * FeatureType.
	  * @param schema
	  * gvSIG feature schema that contains a list of attributes
	  * @return
	 * @throws SchemaException 
	 * @throws  
	  */
	 public static FeatureType featureTypefromGvSIGToGeotools(LayerDefinition lyrDef) throws Exception{
		 FieldDescription[] fDescription = lyrDef.getFieldsDesc();
		 AttributeType[] types = new AttributeType[fDescription.length];
		 for(int i=0 ; i<fDescription.length ; i++){
			 types[i] = attributteTypefromGvSIGToGeotools(fDescription[i]);
		 }
		 String typeName = lyrDef.getName();
		 FeatureType featureType = FeatureTypeBuilder.newFeatureType(types,typeName);					
		 return featureType;
	 }	 
	 
	 
	/**
	 * Make a conversion between the gvSIG FieldDescription and a geotools
	 * AttributeType.
	 * @param fDescription
	 * gvSIG field description
	 * @return
	 */
	 private static AttributeType attributteTypefromGvSIGToGeotools(FieldDescription fDescrition){
		 AttributeType type = AttributeTypeFactory.newAttributeType(fDescrition.getFieldName(),
				 typesFromgvSIGtoGeotools(fDescrition.getFieldType()));
		 
		 return type;
	 }	
	 
	 
	 /**
	  * Type conversor from gvSIG types to geotools types
	  * @param type
	  * @return
	  */
	 private static Class typesFromgvSIGtoGeotools(int type){
		 switch (type) {
         case Types.SMALLINT:
        	 return Integer.class;
         case Types.BIT:
        	 return Boolean.class;
         case Types.BOOLEAN:
        	 return Boolean.class;
         case Types.VARCHAR:
        	 return String.class;
         case Types.DOUBLE:
        	 return Double.class;
         case Types.INTEGER:
        	 return Integer.class;
         default:
        	 return String.class;        
		 }
	 } 
	 
		/**	
		 * From gvSIG types to xlink types used in GML 
		 * @param gmlType
		 * @return
		 */
	 public static String gvSIGToXlinkTypes(int type){
		 switch (type) {
		 case Types.BIT:
			 return "xs:boolean";
		 case Types.TINYINT:
			 return "xs:integer";
		 case Types.SMALLINT:
			 return "xs:integer";
		 case Types.INTEGER:
			 return "xs:double";
		 case Types.BIGINT: 
			 return "xs:integer";
		 case Types.FLOAT:
			 return "xs:float";
		 case Types.REAL:
			 return "xs:double";
		 case Types.DOUBLE:
			 return "xs:double";
		 case Types.NUMERIC:
			 return "xs:integer";
		 case Types.DECIMAL:
			 return "xs:float";
		 case Types.CHAR:
			 return "xs:string";
		 case Types.VARCHAR:
			 return "xs:string";
		 case Types.LONGVARCHAR: 
			 return "xs:string";
		 case Types.DATE:
			 return "xs:string";
		 case Types.TIME:
			 return "xs:string";
		 case Types.TIMESTAMP:
			 return "xs:string";
		 case Types.BINARY:
			 return "xs:boolean";
		 case Types.VARBINARY:
			 return "xs:string";
		 case Types.LONGVARBINARY:
			 return "xs:string";
		 case Types.NULL:
			 return "xs:string";
		 case Types.OTHER:
			 return "xs:string";
		 case Types.BOOLEAN:
			 return "xs:boolean";
         default:
        	 return "xs:string";        
		 }
	 }
	 
		/**	
		 * From gvSIG to GML types
		 * @param gmlType
		 * @return
		 */
	 public static String gvSIGToGMLTypes(int type){
		 switch (type) {
         case FShape.LINE:
        	 return "gml:MultiLineStringPropertyType";
         case FShape.POINT:
           	 return "gml:PointPropertyType";
         case FShape.MULTIPOINT:
        	 return "gml:MultiPointPropertyType";
         case FShape.POLYGON:
        	 return "gml:MultiPolygonPropertyType";
         default:
        	 return "gml:GeometryPropertyType";        
		 }        	
	 }	
	
	/**
	 * From Java to gvSIG types
	 * @param type
	 * @return
	 */
	public static int javaToGvSIGTypes(Class type){
		if (type == String.class){
			return Types.VARCHAR;
		}
		if (type == Integer.class){
			return Types.INTEGER;
		}
		if (type == Double.class){
			return Types.DOUBLE;
		}
		if (type == Float.class){
			return Types.FLOAT;
		}
		if (type == Long.class){
			return Types.INTEGER;
		}
		return Types.VARCHAR;
		
	}


	public static int gmlToGvSigType(String gmlType) {
		if (gmlType.toUpperCase().compareTo("STRING") == 0){
			return Types.VARCHAR;
		}
		if (gmlType.toUpperCase().compareTo("\"\"") == 0){
			return Types.VARCHAR;
		}
		if (gmlType.toUpperCase().compareTo("INTEGER") == 0){
			return  Types.INTEGER;
		}
		if (gmlType.toUpperCase().compareTo("INT") == 0){
			return Types.INTEGER;
		}
		if (gmlType.toUpperCase().compareTo("LONG") == 0){
			return Types.INTEGER;
		}
		if (gmlType.toUpperCase().compareTo("DOUBLE") == 0){
			return Types.DOUBLE;
		}
		if (gmlType.toUpperCase().compareTo("FLOAT") == 0){
			return Types.DOUBLE;
		}
		return Types.VARCHAR;		
	}
}
