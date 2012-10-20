package org.gvsig.fmap.drivers.gpe.utils;

import java.util.Date;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.vividsolutions.jts.geom.Geometry;

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
 * $Id: GMLUtils.java 10627 2007-03-06 17:10:21Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 17:08:56  caballero
 * Exceptions
 *
 * Revision 1.1  2006/07/19 12:29:39  jorpiell
 * Añadido el driver de GML
 *
 *
 */
/**
 * Some utils to manage GML
 * 
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class GMLUtils {

	public static Value[] getValues(Object[] attr) {
		Value[] values = null;
		if (attr.length - 1 > 0) {
			values = new Value[attr.length - 1];
		} else {
			values = new Value[0];
		}
		for (int i = 1; i < attr.length; i++) {
			if (attr[i] != null) {
				if (attr[i] instanceof Double) {
					values[i - 1] = ValueFactory.createValue(((Double) attr[i])
							.doubleValue());
				} else if (attr[i] instanceof String) {
					values[i - 1] = ValueFactory.createValue(String
							.valueOf(attr[i]));
				} else if (attr[i] instanceof Long) {
					values[i - 1] = ValueFactory.createValue(((Long) attr[i])
							.longValue());
				} else if (attr[i] instanceof Integer) {
					values[i - 1] = ValueFactory
							.createValue(((Integer) attr[i]).intValue());
				} else if (attr[i] instanceof Float) {
					values[i - 1] = ValueFactory.createValue(((Float) attr[i])
							.floatValue());
				} else if (attr[i] instanceof Short) {
					values[i - 1] = ValueFactory.createValue(((Short) attr[i])
							.shortValue());
				} else if (attr[i] instanceof Boolean) {
					values[i - 1] = ValueFactory
							.createValue(((Boolean) attr[i]).booleanValue());
				} else if (attr[i] instanceof Date) {
					values[i - 1] = ValueFactory.createValue(((Date) attr[i]));
				}
			} else {
				values[i - 1] = ValueFactory.createValue("");
			}
		}
		return values;
	}

	/**
	 * It return true is the attributeType is a geometry
	 * 
	 * @param attributeType
	 *            Type to compare
	 * @return
	 */
	public static boolean isGeometry(String attributeType) {
		if (attributeType == null) {
			return false;
		}

		if (attributeType.compareTo("gml:GeometryPropertyType") == 0) {
			return true;
		}
		if (attributeType.compareTo("gml:MultiLineStringPropertyType") == 0) {
			return true;
		}
		if (attributeType.compareTo("gml:PointPropertyType") == 0) {
			return true;
		}
		if (attributeType.compareTo("gml:MultiPointPropertyType") == 0) {
			return true;
		}
		if (attributeType.compareTo("gml:MultiPolygonPropertyType") == 0) {
			return true;
		}
		return false;
	}

	/**
	 * It return true is the attributeType is a geometry
	 * 
	 * @param attributeType
	 *            Type to compare
	 * @return
	 */
	public static boolean isGeometry(Class attributeType) {
		return Geometry.class.isAssignableFrom(attributeType);
	}

}
