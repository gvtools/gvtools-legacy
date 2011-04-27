package org.gvsig.symbology.fmap.rendering.filter.operations;

import java.util.ArrayList;
import java.util.Hashtable;

import com.hardcode.gdbms.engine.values.Value;
/**
 * Implements the functionality of a null constant
 *
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 *
 */
public class NullConstant extends Operator{



	public String getName() {
		return OperationTags.NULL_CONSTANT;
	}

	public NullConstant(Hashtable<String, Value> symbol_table) {
		super(symbol_table);
	}

	public void addArgument(int i, Expression arg) {

		throw new RuntimeException("Cannot add an argument to a constant");

	}

	public void check() throws ExpressionException {
		return;
	}

	public Object evaluate() throws ExpressionException {
		return null;
	}

	public ArrayList<Expression> getArguments() {
		return null;
	}

	public String getPattern() {
		return null;
	}

	public void setArguments(ArrayList<Expression> arguments) {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");

	}

}
