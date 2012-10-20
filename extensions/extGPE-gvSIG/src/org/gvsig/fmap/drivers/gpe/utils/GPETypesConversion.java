package org.gvsig.fmap.drivers.gpe.utils;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

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
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class GPETypesConversion {

	/**
	 * Convert types from java to gvSIG
	 * 
	 * @param obj
	 * @return
	 */
	public static Value fromJavaTogvSIG(Object obj) {
		if (obj instanceof String) {
			return ValueFactory.createValue((String) obj);
		}
		if (obj instanceof Integer) {
			return ValueFactory.createValue(((Integer) obj).intValue());
		}
		if (obj instanceof Double) {
			return ValueFactory.createValue(((Double) obj).doubleValue());
		}
		if (obj instanceof Float) {
			return ValueFactory.createValue(((Float) obj).floatValue());
		}
		if (obj instanceof Long) {
			return ValueFactory.createValue(((Long) obj).longValue());
		}
		if (obj instanceof Boolean) {
			return ValueFactory.createValue(((Boolean) obj).booleanValue());
		}
		return ValueFactory.createValue("");
	}
}
