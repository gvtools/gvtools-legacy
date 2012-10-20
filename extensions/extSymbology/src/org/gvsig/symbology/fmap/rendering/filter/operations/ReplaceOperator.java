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

import com.hardcode.gdbms.engine.values.BinaryValue;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.ByteValue;
import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.FloatValue;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.LongValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.ShortValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;

/**
 * 
 * Implements the funcionality of the ReplaceOperator operator which takes an
 * string with a field name and returns its value contained in a HashTable
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 * 
 */
public class ReplaceOperator extends Operator {

	private String id;

	public String getName() {
		return OperationTags.REPLACE_OP;
	}

	public ReplaceOperator(Hashtable<String, Value> symbol_table) {
		super(symbol_table);
	}

	public void addArgument(String image) {
		id = image;

	}

	public void addArgument(int i, Expression arg) {
		// TODO Auto-generated method stub

	}

	public Object evaluate() throws ExpressionException {

		Value v = symbol_table.get(id);
		if (v != null) {
			if (v.getClass().equals(IntValue.class)) {
				Integer val = Integer.valueOf(v.toString());
				return (val);
			}
			if (v.getClass().equals(LongValue.class)
					|| v.getClass().equals(ByteValue.class)
					|| v.getClass().equals(ShortValue.class)
					|| v.getClass().equals(FloatValue.class)
					|| v.getClass().equals(DoubleValue.class)) {
				Double val = Double.valueOf(v.toString());
				return (val);
			} else if (v.getClass().equals(BooleanValue.class)
					|| v.getClass().equals(BinaryValue.class)) {
				return (new Boolean(v.toString()));
			} else if (v.getClass().equals(StringValue.class)) {
				return (new String(v.toString()));
			} else if (v.getClass().equals(NullValue.class)) {
				return (null);
			}
		} else if (v == null && id.length() > 0)
			return (new String("[" + id.toString() + "]"));

		throw new ExpressionException(ExpressionException.NO_CLASSIF_NAME);

	}

	public ArrayList<Expression> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPattern() {
		return null;
	}

	public void setArguments(ArrayList<Expression> arguments) {
		// TODO Auto-generated method stub

	}

	public void check() throws ExpressionException {
		return;
	}

	public String getValue() {
		return id;
	}

}
