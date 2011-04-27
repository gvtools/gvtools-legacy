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
package org.gvsig.symbology.fmap.labeling.parse;

import java.io.CharArrayReader;
import java.io.StringReader;
import java.util.Hashtable;

import junit.framework.TestCase;


import org.gvsig.symbology.fmap.labeling.lang.functions.SubstringFunction;
import org.gvsig.symbology.fmap.rendering.filter.operations.Expression;
import org.gvsig.symbology.fmap.rendering.filter.operations.ExpressionException;
import org.gvsig.symbology.fmap.rendering.filter.operations.OperatorsFactory;

import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

public class TestLabelExpressionParser extends TestCase {
	LabelExpressionParser parser;
	Hashtable<String, Value> symbols_table = new Hashtable<String, Value>();

	protected void setUp() throws Exception {
		super.setUp();

		for (int j = 0; j < symbols.length; j++) {
			symbols_table.put(symbols[j].id,symbols[j].val);
		}
	}

	private class Symbol {
		private String id;
		private Value val;

		public Symbol(String id, Value val) {
			this.id = id;
			this.val = val;
		}
	}
	private Symbol[] symbols = new Symbol[] {
// 0
			new Symbol(
					"A_DOUBLE_VALUE",
					ValueFactory.createValue(4.0)
			),
// 1
			new Symbol(
					"ANOTHER_DOUBLE_VALUE",
					ValueFactory.createValue(Math.PI)
			),
// 2
			new Symbol(
					"A_INTEGER_VALUE",
					ValueFactory.createValue(12)
			),
// 3
			new Symbol(
					"A_STRING_VALUE",
					ValueFactory.createValue("this is a String")
			),
// 4
			new Symbol(
					"A_BOOLEAN_VALUE",
					ValueFactory.createValue(true)
			),
// 5
			new Symbol(
					"ANOTHER_BOOLEAN_VALUE",
					ValueFactory.createValue(false)
			),
// 6
			new Symbol(
					"A_NULL_VALUE",
					ValueFactory.createNullValue()
			),

// 7
			new Symbol(
					"XX",
					ValueFactory.createValue(2)),
// 8
			new Symbol(
					"Y",
					ValueFactory.createValue(2.0)),

	};

	private String[] ArgumentsExpressions = new String[] {
			"(754)",
			"(754, \"blah\")",
			"(-9.68, [A_DOUBLE_VALUE]) ",
			"(false, true) ",
	};
	private Object[][] ArgumentsExpressionsValue = new Object[][]  {
			new Object[] { 754 },
			new Object[] { 754, "blah" },
			new Object[] { -9.68, 4.0 },
			new Object[] { false, true },
	};

	public void testArguments() throws ExpressionException {

		String[] expressions = ArgumentsExpressions;
		Object[][] expectedValues = ArgumentsExpressionsValue;
		System.out.println("\nTestLabelExpressionParser.testArgumens()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);

			System.out.print("Parsing '"+expr+"'");
			try {

				int amount = parser.Arguments();

				System.out.print(": [ parsed ]. Evaluating: ");
				Object[] expected = expectedValues[i];
				assertTrue("Failed detecting argument number. Expected "+expected.length+", but got "+amount+".", amount == expected.length);

				for (int j = expected.length-1; j >= 0; j--) { // <- Last in first out
					Object value = parser.pop();
					assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected[j]+"]", value.equals(expected[j]));
					System.out.print(value+" [ Ok! ]\t");
				}


			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}

	private String[] LiteralExpressions = new String[] {
			"true",
			"false",
			" 43564356435.234",
			" 12455 ",
			" \"a string\"",
	};
	private Object[] LiteralExpressionsValue = new Object[]  {
			true ,
			false,
			43564356435.234,
			12455,
			"a string"
	};

	public void testLiteral() throws ExpressionException {
		String[] expressions = LiteralExpressions;
		Object[] expectedValues = LiteralExpressionsValue;
		System.out.println("\nTestLabelExpressionParser.testLiteral()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);

			System.out.print("Parsing '"+expr+"'");
			try {
				parser.UnaryElement();
				System.out.print(": [ parsed ]. Evaluating: ");
				Object expected = expectedValues[i];

				Object value = parser.pop();
				assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected+"]", value.equals(expected));
				System.out.print(value+" [ Ok! ]\t");


			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}

	private String[] FunctionExpressions = new String[] {
			new SubstringFunction(symbols_table).getName()+"(\"My taylor is rich!\", 1)",
			new SubstringFunction(symbols_table).getName()+"(\"My taylor is rich!\", 3, 9)",
			new SubstringFunction(symbols_table).getName()+"("+new SubstringFunction(symbols_table).getName()+"(\"My taylor is rich!\", 3, 9), 2,3)",
	};
	private Object[] FunctionExpressionsValue = new Object[]  {
			"y taylor is rich!" ,
			"taylor" ,
			"y",
	};
	public void testFunction() throws ExpressionException {
		OperatorsFactory operators = OperatorsFactory.getInstance();
		operators.addOperator(SubstringFunction.class);

		String[] expressions = FunctionExpressions;
		Object[] expectedValues = FunctionExpressionsValue;
		System.out.println("\nTestLabelExpressionParser.testFunction()");


		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);
			parser.setOperatorsFactory(operators);

			System.out.print("Parsing '"+expr+"'");
			try {
				parser.function();
				System.out.print(": [ parsed ]. Evaluating: ");
				Object expected = expectedValues[i];

				Object value = parser.pop();
				assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected+"]", value.equals(expected));
				System.out.print(value+" [ Ok! ]\t");


			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}

	public void testPrimitiveExpression() throws ExpressionException {
		String[] PrimitiveExpressions = new String[symbols.length];
		Object[] PrimitiveExpressionsValue = new Object[symbols.length];
		for (int i = 0; i < symbols.length; i++) {
			PrimitiveExpressions[i] = "["+symbols[i].id+"]";
			PrimitiveExpressionsValue[i] = symbols[i].val;
		}

		String[] expressions = PrimitiveExpressions;
		Object[] expectedValues = PrimitiveExpressionsValue;

		System.out.println("\nTestLabelExpressionParser.testPrimitiveExpression()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);

			System.out.print("Parsing '"+expr+"'");
			try {
				parser.Element();
				System.out.print(": [ parsed ]. Evaluating: ");
				Object expected = expectedValues[i];

				// GDBMS values are evaluated as java primitives
				if (expected instanceof DoubleValue) {
					expected = ((DoubleValue) expected).getValue();
				} else if (expected instanceof IntValue) {
					expected = ((IntValue) expected).getValue();
				} else if (expected instanceof StringValue) {
					expected = ((StringValue) expected).getValue();
				}

//				else if (expected instanceof DateValue) {
//				expected = ((DateValue) expected).getValue();
//				}

				else if (expected instanceof BooleanValue) {
					expected = ((BooleanValue) expected).getValue();
				} else if (expected instanceof NullValue) {
					expected = null;
				}


				Object value = parser.pop();
				assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected+"]", value == null ? value == expected : value.equals(expected));
				System.out.print(value+" [ Ok! ]\t");


			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}


	private String[] UnaryBooleanExpressions = new String[] {
			"!false",
			"!true",
			"![A_BOOLEAN_VALUE]",
			"![ANOTHER_BOOLEAN_VALUE]",
	};
	private Object[] UnaryBooleanExpressionsValue = new Object[]  {
			!false,
			!true,
			!((BooleanValue) symbols[4].val).getValue(),
			!((BooleanValue) symbols[5].val).getValue(),
	};
	public void testBooleanUnaryExpression() throws ExpressionException {
		String[] expressions = UnaryBooleanExpressions;
		Object[] expectedValues = UnaryBooleanExpressionsValue;
		System.out.println("\nTestLabelExpressionParser.testBooleanUnaryExpression()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);

			System.out.print("Parsing '"+expr+"'");
			try {
				parser.UnaryElement();
				System.out.print(": [ parsed ]. Evaluating: ");
				Object expected = expectedValues[i];

				Object value = parser.pop();
				assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected+"]", value.equals(expected));
				System.out.print(value+" [ Ok! ]\t");


			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}


	private String[] UnaryArithmeticExpressions = new String[] {
			"+65",
			"-5.5",
			"+-+-[A_DOUBLE_VALUE]",
			"--+[ANOTHER_DOUBLE_VALUE]",
	};
	private Object[] UnaryArithmeticExpressionsValue = new Object[]  {
			65,
			-5.5,
			((DoubleValue) symbols[0].val).getValue(),
			((DoubleValue) symbols[1].val).getValue(),
	};
	public void testArithmeticUnaryExpression() throws ExpressionException {
		String[] expressions = UnaryArithmeticExpressions;
		Object[] expectedValues = UnaryArithmeticExpressionsValue;
		System.out.println("\nTestLabelExpressionParser.testArithmeticUnaryExpression()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);

			System.out.print("Parsing '"+expr+"'");
			try {
				parser.MultiplicativeExpression();
				System.out.print(": [ parsed ]. Evaluating: ");
				Object expected = expectedValues[i];

				Object value = parser.pop();
				assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected+"]", value.equals(expected));
				System.out.print(value+" [ Ok! ]\t");


			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}

	private String[] MultiplicativeArithmeticExpressions = new String[] {
			"3*4356.234",
			"15*10",
			"15/10",
			"8768758/2432432",
			"[A_DOUBLE_VALUE]*[ANOTHER_DOUBLE_VALUE]",
			"[A_DOUBLE_VALUE]/[ANOTHER_DOUBLE_VALUE]",
			"[A_DOUBLE_VALUE]*16",
	};
	private Object[] MultiplicativeArithmeticExpressionsValue = new Object[]  {
			3*4356.234,
			15*10,
			15/10,
			8768758/2432432,
			((DoubleValue) symbols[0].val).getValue()*((DoubleValue) symbols[1].val).getValue(),
			((DoubleValue) symbols[0].val).getValue()/((DoubleValue) symbols[1].val).getValue(),
			((DoubleValue) symbols[0].val).getValue() * 16,
	};

	public void testMultiplicativeExpression() throws ExpressionException {
		String[] expressions = MultiplicativeArithmeticExpressions;
		Object[] expectedValues = MultiplicativeArithmeticExpressionsValue;
		System.out.println("\nTestLabelExpressionParser.testMultiplicativeExpression()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);

			System.out.print("Parsing '"+expr+"'");
			try {
				parser.MultiplicativeExpression();
				System.out.print(": [ parsed ]. Evaluating: ");
				Object expected = expectedValues[i];

				Object value = parser.pop();
				assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected+"]", value.equals(expected));
				System.out.print(value+" [ Ok! ]\t");


			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}

	private String[] AdditiveArithmeticExpressions = new String[] {
			"3-4356.234",
			"8768758+2432432",
			"[A_DOUBLE_VALUE]+[ANOTHER_DOUBLE_VALUE]",
			"[A_DOUBLE_VALUE]-[ANOTHER_DOUBLE_VALUE]",
	};
	private Object[] AdditiveArithmeticExpressionsValue = new Object[]  {
			3-4356.234,
			8768758+2432432,
			((DoubleValue) symbols[0].val).getValue()+((DoubleValue) symbols[1].val).getValue(),
			((DoubleValue) symbols[0].val).getValue()-((DoubleValue) symbols[1].val).getValue(),
	};
	public void testAdditiveExpression() throws ExpressionException {
		String[] expressions = AdditiveArithmeticExpressions;
		Object[] expectedValues = AdditiveArithmeticExpressionsValue;
		System.out.println("\nTestLabelExpressionParser.testAdditiveExpression()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);

			System.out.print("Parsing '"+expr+"'");
			try {
				parser.AdditiveExpression();
				System.out.print(": [ parsed ]. Evaluating: ");
				Object expected = expectedValues[i];

				Object value = parser.pop();
				assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected+"]", value.equals(expected));
				System.out.print(value+" [ Ok! ]\t");


			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}



	private String EOField = LabelExpressionParser.tokenFor(LabelExpressionParser.EOFIELD);
	private String EOExpression = LabelExpressionParser.tokenFor(LabelExpressionParser.EOEXPR);
	private String DOUBLEQUOTE = "\"";

	private String[] FieldExpressionStrings = new String[] {
			"km" +"25",
			DOUBLEQUOTE+"km"+DOUBLEQUOTE+" "+DOUBLEQUOTE+" 25"+DOUBLEQUOTE,
			"[A_STRING_VALUE] "+DOUBLEQUOTE+" 25"+DOUBLEQUOTE,
			"[A_INTEGER_VALUE] "+DOUBLEQUOTE+" 25"+DOUBLEQUOTE,
			"[A_DOUBLE_VALUE] "+DOUBLEQUOTE+" 25"+DOUBLEQUOTE,
			"2+3"+"km",
	};
	private Object[] FieldExpressionValue = new Object[]  {
			"km25",
			"km 25",
			((StringValue) symbols[3].val).getValue()+" 25",
			((IntValue) symbols[2].val).getValue()+" 25",
			((DoubleValue) symbols[0].val).getValue()+" 25",
			"5km"
	};

	public void testFieldExpressionStrings() throws ExpressionException {

		String[] expressions = FieldExpressionStrings;
		Object[] expectedValues = FieldExpressionValue;
		System.out.println("\nTestLabelExpressionParser.testConcatExpressions()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);


			System.out.print("Parsing '"+expr+"'");
			try {

				parser.LabelExpression();

				System.out.print(": [ parsed ]. Evaluating: ");
				Object[] expected = expectedValues;
				String myValues = ((Expression)parser.getStack().pop()).evaluate().toString();


				Object value = myValues;
				assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected[i]+"]", value.equals(expected[i].toString()));
				System.out.print(value+" [ Ok! ]\t");



			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}


	private String[] LabelExpressions = new String[] {
			"1+2"+EOField+"true"+EOField+"2<3",
			"300+454"+EOField+"false",
			"1+2"+EOField+"1+5"+EOField+"2<3",
			"[A_DOUBLE_VALUE]/[ANOTHER_DOUBLE_VALUE]"+EOField+"Km",
			"km" +"25"+EOField+"1+5",
			DOUBLEQUOTE+"km"+DOUBLEQUOTE+" "+DOUBLEQUOTE+" 25"+DOUBLEQUOTE+EOField+"1+2"+EOField+"true"+EOField+"2<3",

	};
	private Object[][] LabelExpressionsValue = new Object[][] {
			new Object[] {3,true,true },
			new Object[] { 754, false },
			new Object[] { 3,6,true },
			new Object[] {((DoubleValue) symbols[0].val).getValue()/((DoubleValue) symbols[1].val).getValue(),"Km"},
			new Object[] {"km25",6 },
			new Object[] {"km 25",3,true,true },

	};

	public void testLabelExpressions() throws ExpressionException {

		String[] expressions = LabelExpressions;
		Object[][] expectedValues = LabelExpressionsValue;
		System.out.println("\nTestLabelExpressionParser.testLabelExpressions()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);


			System.out.print("Parsing '"+expr+"'");
			try {

				parser.LabelExpression();

				System.out.print(": [ parsed ]. Evaluating: ");
				Object[] expected = expectedValues[i];
				Object[] myValues = (Object[]) ((Expression)parser.getStack().pop()).evaluate();

				for (int j = 0; j < myValues.length; j++) { // <- Last in first out
					Object value = myValues[j];
					assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected[j]+"]", value.equals(expected[j].toString()));
					System.out.print(value+" [ Ok! ]\t");
				}


			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}



	private String[] SolvedBugsExpressions = new String[] {
			"([Y] + 3) * 5;",
			"([Y] + 3) * 5"+EOField+"([Y] + 3) * 5",
	};
	private Object[][] SolvedBugsExpressionsValue = new Object[][] {
			new Object[] { ((((DoubleValue) symbols[8].val ).getValue() + 3 ) *5) },
			new Object[] {
					""+((((DoubleValue) symbols[8].val ).getValue() + 3 ) *5),
					""+((((DoubleValue) symbols[8].val ).getValue() + 3 ) *5) },
	};
	/**
	 * Tests several buggy expressions detected by testers and users
	 * @throws ExpressionException
	 */
	public void testSolvedBugsExpressions() throws ExpressionException {

		String[] expressions = SolvedBugsExpressions;
		Object[][] expectedValues = SolvedBugsExpressionsValue;
		System.out.println("\nTestLabelExpressionParser.testSolvedBugsExpressions()");

		for (int i = 0; i < expressions.length; i++) {
			String expr = expressions[i];
			parser = new LabelExpressionParser(new StringReader(expr),symbols_table);


			System.out.print("Parsing '"+expr+"'");
			try {

				parser.LabelExpression();

				System.out.print(": [ parsed ]. Evaluating: ");
				Object[] expected = expectedValues[i];
				Object value = ((Expression)parser.getStack().pop()).evaluate();
				if (value.getClass().isArray()) {
					Object[] myValues = (Object[]) value;

					for (int j = 0; j < myValues.length; j++) { // <- Last in first out
						Object aValue = myValues[j];
						assertTrue("Parsed text '"+expr+"' evaluated to ("+aValue+") when expecting ["+expected[j]+"]", aValue.equals(expected[j].toString()));
						System.out.print(aValue+" [ Ok! ]\t");
					}
				} else {
					assertTrue("Parsed text '"+expr+"' evaluated to ("+value+") when expecting ["+expected+"]", value.equals(expected[0]));
					System.out.print(value+" [ Ok! ]\t");
				}

			} catch (ParseException e) {
				System.err.println(":\t [ Fail! ]");
				fail("Failed parsing text '"+expr+"'\n"+e.getMessage());
			}
			System.out.println();
		}
	}

}
