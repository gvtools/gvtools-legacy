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
package org.gvsig.symbology.fmap.rendering.filter.operations;

import java.util.ArrayList;
import java.util.Hashtable;

import com.hardcode.gdbms.engine.values.Value;

public class FieldExpressionOperator extends Operator {

	private ArrayList<Expression> arguments = new ArrayList<Expression>();

	public FieldExpressionOperator(Hashtable<String, Value> symbol_table) {
		super(symbol_table);
	}

	public void addArgument(int i, Expression arg) {
		arguments.add(i, arg);
	}

	public void check() throws ExpressionException {
	}

	public Object evaluate() throws ExpressionException {

		String result = "";

		if (arguments != null) {

			for (int i = arguments.size() - 1; i >= 0; i--) {
				Expression function = (Expression) arguments.get(i);
				Object value1 = function.evaluate();
				if (value1 == null)
					value1 = "";
				result += ((String) (value1.toString()));
			}
			return result;
		}

		return null;
	}

	public ArrayList<Expression> getArguments() {
		return arguments;
	}

	public String getName() {
		return null;
	}

	public String getPattern() {
		throw new Error("Not yet implemented");
	}

	public void setArguments(ArrayList<Expression> arguments) {
		this.arguments = arguments;
	}

}
