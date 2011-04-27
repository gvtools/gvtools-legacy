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

import java.util.ArrayList;
import java.util.Hashtable;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.Messages;

/**
 * Implements the funcionality of the negation operator which only can
 * be used in boolean expressions
 *
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 *
 */
public class NotOperator extends Operator {


	private ArrayList<Expression> arguments = new ArrayList<Expression>();

	public String getName() {
		return OperationTags.NOT_OP;
	}

	public NotOperator(Hashtable<String, Value> symbol_table) {
		super(symbol_table);
	}

	public Object evaluate()throws ExpressionException {

		if(arguments.size() > 1)
			throw new ExpressionException(ExpressionException.INCORRECT_NUMBER_OF_ARGUMENTS);
		Object eval=((Expression)arguments.get(0)).evaluate();
		if (eval==null)
			return false;
		boolean result = ((Boolean)eval).booleanValue();
		if (result)
			return false;
		else
			return true;
	}


	public Class getResultType()
	{
		return Boolean.class;
	}

	public void addArgument(int i, Expression arg) {

		arguments.add(i, arg);

	}


	public String getPattern() {
		return OperationTags.NOT_OP +"("+
			Messages.getString(OperationTags.OPERAND)+")\n"+
			Messages.getString(OperationTags.OPERAND)+" = "+
			Messages.getString(OperationTags.BOOLEAN_VALUE);
	}

	public ArrayList<Expression> getArguments() {
		return arguments;
	}

	public void setArguments(ArrayList<Expression> arguments) {
		this.arguments = arguments;
	}


	public void check() throws ExpressionException {
		if (arguments.size() != 1)
			throw new ExpressionException(ExpressionException.INCORRECT_NUMBER_OF_ARGUMENTS);

		if(! (arguments.get(0).evaluate() instanceof Boolean))
			throw new ExpressionException(ExpressionException.CLASS_CASTING_EXCEPTION);

	}

}
