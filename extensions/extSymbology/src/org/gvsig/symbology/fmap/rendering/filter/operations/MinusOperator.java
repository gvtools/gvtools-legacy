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
 * Implements the funcionality of the minus operator
 *
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 *
 */

public class MinusOperator extends Operator {



	private ArrayList<Expression> arguments = new ArrayList<Expression>();

	public String getName() {
		return OperationTags.MINUS_OP;
	}


	public MinusOperator(Hashtable<String, Value> symbol_table) {
		super(symbol_table);
	}

	public Object evaluate()throws ExpressionException {

		Double doubleResult = null;
		Integer intResult = null;


		try {

			intResult = (Integer)((Expression)arguments.get(0)).evaluate();
			if (intResult == null){
				intResult=new Integer(0);
			}
			if (arguments.size() == 1)
				return -1*intResult;

			for (int i = 1; i < arguments.size(); i++){
				Expression function = (Expression)arguments.get(i);
				Integer eval=((Integer)function.evaluate());
				if (eval==null)
					eval=new Integer(0);
				intResult -= eval;
			}

			return intResult;


		} catch (ClassCastException e) {

			doubleResult = new Double(((Expression)arguments.get(0)).evaluate().toString());
			if (doubleResult == null){
				doubleResult=new Double(0);
			}
			if(arguments.size() == 1)
				return -1*doubleResult;
			for (int i = 1; i < arguments.size(); i++){
				Expression function = (Expression)arguments.get(i);
				Double eval=(Double)((Expression)function).evaluate();
				if (eval==null)
					eval=new Double(0);
				doubleResult -= new Double(eval.toString());
			}

			return doubleResult;
		}

	}

	public void addArgument(int i, Expression arg) {
		arguments.add(i, arg);
	}


	public String getPattern() {
		return "("+Messages.getString(OperationTags.OPERAND)
		+ OperationTags.MINUS_OP +Messages.getString(OperationTags.OPERAND)+ ")\n"+
		Messages.getString(OperationTags.OPERAND) +" = "+
		Messages.getString(OperationTags.NUMERIC_VALUE );
	}

	public ArrayList<Expression> getArguments() {
		return arguments;
	}

	public void setArguments(ArrayList<Expression> arguments) {
		this.arguments = arguments;
	}

	public void check() throws ExpressionException {

		for (int i = 0; i < arguments.size(); i++) {
			if(!(arguments.get(i)instanceof NumericalConstant))
				throw new ExpressionException(ExpressionException.CLASS_CASTING_EXCEPTION);
		}
	}

}
