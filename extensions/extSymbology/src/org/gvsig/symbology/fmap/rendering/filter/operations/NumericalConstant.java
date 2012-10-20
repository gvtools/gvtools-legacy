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
 * Implements the funcionality of a double constant
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 * 
 */
public class NumericalConstant extends Operator {
	private double doubleValue;
	private int integerValue;
	private boolean isDoubleValue = false;

	public String getName() {
		return OperationTags.NUMERIC_VALUE;
	}

	public NumericalConstant(Object value, Hashtable<String, Value> symbol_table) {

		super(symbol_table);

		if (value instanceof Integer) {
			integerValue = Integer.valueOf(value.toString());
		} else if (value instanceof Double) {
			doubleValue = Double.valueOf(value.toString());
			isDoubleValue = true;
		}

	}

	public Object evaluate() {

		if (isDoubleValue)
			return doubleValue;
		return integerValue;

	}

	public String getDescription() {
		return Messages.getString(OperationTags.CONSTANT);
	}

	public Class getResultType() {
		return Integer.class;
	}

	public void addArguments(ArrayList<Expression> expressions) {

		throw new RuntimeException("Cannot add arguments to a constant");

	}

	public void addArgument(int i, Expression arg) {

		throw new RuntimeException("Cannot add arguments to a constant");
	}

	public String getPattern() {
		return null;
	}

	public ArrayList<Expression> getArguments() {
		return null;
	}

	public void setArguments(ArrayList<Expression> arguments) {
		return;
	}

	public void check() throws ExpressionException {
		return;
	}

	public Object getValue() {
		if (isDoubleValue)
			return doubleValue;
		return integerValue;
	}

	public boolean isDoubleValue() {
		return isDoubleValue;
	}

	public void setDoubleValue(boolean isDoubleValue) {
		this.isDoubleValue = isDoubleValue;
	}
}
