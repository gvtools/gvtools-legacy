/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id: 
 * $Log: 
 */
package com.iver.cit.gvsig.geoprocess.util;

import java.lang.reflect.Array;

/**
 * Utility class to compute hash codes for classes that overwrites equals.
 * 
 * Inspired in http://www.javapractices.com/topic/TopicAction.do?Id=28
 * 
 * @author Alvaro Zabala
 * 
 */
public class HashCodeUtil {

	/**
	 * An initial value for a <code>hashCode</code>, to which is added
	 * contributions from fields. Using a non-zero value decreases collisons of
	 * <code>hashCode</code> values.
	 */
	public static final int SEED = 23;

	private static final int FODD_PRIME_NUMBER = 37;

	public static int hashCode(int seed, int valueInt) {
		return firstTerm(seed) + valueInt;
	}

	public static int hashCode(int seed, Object obj) {
		int result = seed;
		if (obj == null) {
			result = hashCode(result, 0);
		} else if (!isArray(obj)) {
			result = hashCode(result, obj.hashCode());
		} else {
			int length = Array.getLength(obj);
			for (int idx = 0; idx < length; ++idx) {
				Object item = Array.get(obj, idx);
				result = hashCode(result, item);
			}
		}
		return result;
	}

	private static int firstTerm(int aSeed) {
		return FODD_PRIME_NUMBER * aSeed;
	}

	private static boolean isArray(Object aObject) {
		return aObject.getClass().isArray();
	}
}
