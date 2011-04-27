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
package org.gvsig.symbology.fmap.labeling.lang.functions;

import java.util.ArrayList;
import java.util.Hashtable;

import org.gvsig.symbology.fmap.rendering.filter.operations.Expression;
import org.gvsig.symbology.fmap.rendering.filter.operations.ExpressionException;
import org.gvsig.symbology.fmap.rendering.filter.operations.OperationTags;
import org.gvsig.symbology.fmap.rendering.filter.operations.Operator;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.Messages;

public class IndexOfFunction extends Operator {



	ArrayList<Expression> arguments = new ArrayList<Expression>();

	public String getName() {
		return "IndexOf";
	}

	public IndexOfFunction(Hashtable<String, Value> symbol_table) {
		super(symbol_table);
	}

	public void addArgument(int i, Expression arg) {
		arguments.add(i, arg);
	}

	public void check()
	throws ExpressionException {

		if(arguments.size() > 2)
			throw new ExpressionException(ExpressionException.INCORRECT_NUMBER_OF_ARGUMENTS);

	}

	public Object evaluate() throws ExpressionException {
		final String inputString;
		final String subString;

//		try {
//			inputString = (String) args[0];
//		} catch (ClassCastException ccEx) {
//			throw new SemanticException(SemanticException.TYPE_MISMATCH);
//		}
//
//		try {
//			subString = (String) args[1];
//		} catch (ClassCastException ccEx) {
//			throw new SemanticException(SemanticException.TYPE_MISMATCH);
//		}

		if(arguments.size() > 2)
			throw new ExpressionException(ExpressionException.INCORRECT_NUMBER_OF_ARGUMENTS);

		inputString = arguments.get(0).evaluate().toString();
		subString = arguments.get(1).evaluate().toString();

		if (inputString == null) return "";

		return inputString.indexOf(subString);
	}

	public ArrayList<Expression> getArguments() {
		return arguments;
	}

	public String getPattern() {
		return Messages.getString(getName())+"("+Messages.getString(OperationTags.OPERAND)
		+ ","+Messages.getString(OperationTags.OPERAND)+ ")\n"+
		Messages.getString(OperationTags.OPERAND) +" = "+
		Messages.getString(OperationTags.STRING_CONSTANT);

	}

	public void setArguments(ArrayList<Expression> arguments) {
		this.arguments = arguments;
	}

}
