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
package org.gvsig.symbology.fmap.rendering.filter.operations;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Utils for operators
 *
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 *
 */
public class OperatorsUtils {

	public static Object[] getOperatorNames() {
		OperatorsFactory myFactory = OperatorsFactory.getInstance();
		Object[] myOperators = myFactory.functions.keySet().toArray();
		return myOperators;
	}

	public static String getPatternByName(String name) {
		OperatorsFactory myFactory = OperatorsFactory.getInstance();

		myFactory.myClass = (Class) myFactory.functions.get(name);
		Constructor[] cons = myFactory.myClass.getDeclaredConstructors();

		try {
			Expression exp = (Expression)cons[0].newInstance(cons[0].getParameterTypes()[0].newInstance());
			return exp.getPattern();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}

		return null;
	}

}
