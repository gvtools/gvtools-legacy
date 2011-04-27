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

public class SubstringFunction extends Operator {


	ArrayList<Expression> arguments = new ArrayList<Expression>();

	public String getName() {
		return "Substring";
	}

	public SubstringFunction(Hashtable<String, Value> symbol_table) {
		super(symbol_table);
	}

	public void addArgument(int i, Expression arg) {
		arguments.add(i, arg);
	}

	public void check()
	throws ExpressionException {

//		try {
//			inputString = (String) args[0];
//		} catch (ClassCastException ccEx) {
//			throw new SemanticException(SemanticException.TYPE_MISMATCH);
//		}
//
//		try {
//			beginIndex = (Integer) args[1];
//		} catch (ClassCastException ccEx) {
//			throw new SemanticException(SemanticException.TYPE_MISMATCH);
//		}
//
//		try {
//			endIndex = (Integer) args[2];
//		} catch (ClassCastException ccEx) {
//			throw new SemanticException(SemanticException.TYPE_MISMATCH);
//		}
//
//		if (endIndex < beginIndex) {
//			throw new EvaluationException("Begining index is greather than the ending index!");
//		}
//

		if(arguments.size() != 3 && arguments.size() !=2)
			throw new ExpressionException(ExpressionException.INCORRECT_NUMBER_OF_ARGUMENTS);

		if (!(arguments.get(1).evaluate()instanceof Integer))
			throw new ExpressionException(ExpressionException.CLASS_CASTING_EXCEPTION);

		if(arguments.size() == 3)
			if (!(arguments.get(2).evaluate()instanceof Integer))
				throw new ExpressionException(ExpressionException.CLASS_CASTING_EXCEPTION);
	}

	public Object evaluate() throws ExpressionException {
		final String inputString;
		final int beginIndex;
		final int endIndex;

		if(arguments.size() != 3 && arguments.size() !=2)
			throw new ExpressionException(ExpressionException.INCORRECT_NUMBER_OF_ARGUMENTS);

		inputString = arguments.get(0).evaluate().toString();
		beginIndex = (Integer) arguments.get(1).evaluate();
		if(arguments.size() != 2) {
			endIndex = (Integer) arguments.get(2).evaluate();


			return inputString.substring(beginIndex, endIndex);
		} else {
			return inputString.substring(beginIndex);
		}

	}

	public ArrayList<Expression> getArguments() {
		return arguments;
	}

	public String getPattern() {
		return Messages.getString(getName())+"("+Messages.getString(OperationTags.STRING_CONSTANT)
		+ ","+Messages.getString(OperationTags.OPERAND) + ","+Messages.getString(OperationTags.OPERAND)+ ")\n"+
		Messages.getString(OperationTags.OPERAND) +" = "+
		Messages.getString(OperationTags.NUMERIC_VALUE);
	}

	public void setArguments(ArrayList<Expression> arguments) {
		this.arguments = arguments;
	}

}
