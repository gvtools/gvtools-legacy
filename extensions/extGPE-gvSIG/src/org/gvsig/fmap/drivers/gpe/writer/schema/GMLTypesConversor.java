package org.gvsig.fmap.drivers.gpe.writer.schema;

import java.sql.Types;

import com.iver.cit.gvsig.fmap.core.FShape;

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
}
