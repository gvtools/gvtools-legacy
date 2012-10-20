package com.hardcode.gdbms.engine.values;

import java.sql.Types;

import junit.framework.TestCase;

public class ComplexValueTest extends TestCase {
	private static String stringTest1 = "<key1>value1</key1>\n<key2>value2</key2>\n<key3>value3</key3>\n";
	private static String stringTest2 = "<key1>value1</key1>\n<key2>value2</key2>\n<key3>value3</key3>\n<sub1>\n    <skey1>svalue1</skey1>\n    <skey2>svalue2</skey2>\n</sub1>";
	private static String stringTest3 = "<key1>value1</key1>\n<key2>value2</key2>\n<key3>value3</key3>\n<sub1>\n    <skey1>svalue1</skey1>\n    <skey2>svalue2</skey2>\n    <sub2>\n    </sub2>\n    <sub3>\n        <sub4>\n            <s4key>s4value</s4key>\n        </sub4>\n    </sub3>\n</sub1>";
	private static String stringTest7 = "<key1 _type=\"IntValue\">1</key1>\n<key2 _type=\"DoubleValue\">2.2</key2>\n<key3 _type=\"BooleanValue\">true</key3>\n<key4 _type=\"IntValue\">4</key4>\n";
	private static String stringTest8 = "<key1 _type=\"IntValue\">1</key1>\n<key2 _type=\"DoubleValue\">2.2</key2>\n<key3 _type=\"BooleanValue\">true</key3>\n<key4 _type=\"IntValue\">4</key4>\n<sub1>\n    <skey1 _type=\"BooleanValue\">false</skey1>\n</sub1>\n";

	private boolean equals(String str1, String str2) {
		str1 = str1.replaceAll("^\\s*", "");
		str1 = str1.replaceAll(">\\n\\s*", ">");

		str2 = str2.replaceAll("^\\s*", "");
		str2 = str2.replaceAll(">\\n\\s*", ">");
		return str1.equals(str2);
	}

	public void test1() {
		ComplexValue value = ValueFactory.createComplexValue(stringTest1);
		assertNotNull(value);

		assertTrue("keyset.size()", value.keySet().size() == 3);

		assertTrue("containstKey('key1')", value.containsKey("key1"));
		assertTrue("containstKey('key2')", value.containsKey("key2"));
		assertTrue("containstKey('key3')", value.containsKey("key3"));

		assertTrue("get('key1') instanceof StringValue",
				value.get("key1") instanceof StringValue);
		assertTrue("get('key2') instanceof StringValue",
				value.get("key2") instanceof StringValue);
		assertTrue("get('key3') instanceof StringValue",
				value.get("key3") instanceof StringValue);

		assertTrue("get('key1').toString()", value.get("key1").toString()
				.equals("value1"));
		assertTrue("get('key2').toString()", value.get("key2").toString()
				.equals("value2"));
		assertTrue("get('key3').toString()", value.get("key3").toString()
				.equals("value3"));

		assertTrue("toString() == original",
				equals(stringTest1, value.toString()));
	}

	public void test2() {
		ComplexValue value = ValueFactory.createComplexValue(stringTest2);
		assertNotNull(value);

		assertTrue("keyset.size()", value.keySet().size() == 4);

		assertTrue("containstKey('key1')", value.containsKey("key1"));
		assertTrue("containstKey('key2')", value.containsKey("key2"));
		assertTrue("containstKey('key3')", value.containsKey("key3"));
		assertTrue("containstKey('sub1')", value.containsKey("sub1"));

		assertTrue("get('key1') instanceof StringValue",
				value.get("key1") instanceof StringValue);
		assertTrue("get('key2') instanceof StringValue",
				value.get("key2") instanceof StringValue);
		assertTrue("get('key3') instanceof StringValue",
				value.get("key3") instanceof StringValue);
		assertTrue("get('sub1') instanceof ComplexValue",
				value.get("sub1") instanceof ComplexValue);

		assertTrue("get('key1').toString()", value.get("key1").toString()
				.equals("value1"));
		assertTrue("get('key2').toString()", value.get("key2").toString()
				.equals("value2"));
		assertTrue("get('key3').toString()", value.get("key3").toString()
				.equals("value3"));

		ComplexValue sub1 = (ComplexValue) value.get("sub1");
		assertNotNull(sub1);
		assertTrue("sub1.keyset.size()", sub1.keySet().size() == 2);
		assertTrue("sub1.containstKey('skey1')", sub1.containsKey("skey1"));
		assertTrue("sub1.containstKey('skey2')", sub1.containsKey("skey2"));

		assertTrue("sub1.get('skey1') instanceof StringValue",
				sub1.get("skey1") instanceof StringValue);
		assertTrue("sub1.get('skey2') instanceof StringValue",
				sub1.get("skey2") instanceof StringValue);

		assertTrue("sub1.get('skey1').toString()", sub1.get("skey1").toString()
				.equals("svalue1"));
		assertTrue("sub1.get('skey2').toString()", sub1.get("skey2").toString()
				.equals("svalue2"));

		assertTrue("toString() == original",
				equals(stringTest2, value.toString()));
	}

	public void test3() {
		ComplexValue value = ValueFactory.createComplexValue(stringTest3);
		assertNotNull(value);

		assertTrue("keyset.size()", value.keySet().size() == 4);

		assertTrue("containstKey('key1')", value.containsKey("key1"));
		assertTrue("containstKey('key2')", value.containsKey("key2"));
		assertTrue("containstKey('key3')", value.containsKey("key3"));
		assertTrue("containstKey('sub1')", value.containsKey("sub1"));
		assertFalse("not containstKey('kk')", value.containsKey("kk"));

		assertTrue("get('key1') instanceof StringValue",
				value.get("key1") instanceof StringValue);
		assertFalse("get('key1') not instanceof ComplexValue",
				value.get("key1") instanceof ComplexValue);
		assertTrue("get('key2') instanceof StringValue",
				value.get("key2") instanceof StringValue);
		assertFalse("get('key2') not instanceof ComplexValue",
				value.get("key2") instanceof ComplexValue);
		assertTrue("get('key3') instanceof StringValue",
				value.get("key3") instanceof StringValue);
		assertTrue("get('sub1') instanceof ComplexValue",
				value.get("sub1") instanceof ComplexValue);

		assertTrue("get('key1').toString()", value.get("key1").toString()
				.equals("value1"));
		assertTrue("get('key2').toString()", value.get("key2").toString()
				.equals("value2"));
		assertTrue("get('key3').toString()", value.get("key3").toString()
				.equals("value3"));

		ComplexValue sub1 = (ComplexValue) value.get("sub1");
		assertNotNull(sub1);
		assertTrue("sub1.keyset.size()", sub1.keySet().size() == 4);
		assertTrue("sub1.containstKey('skey1')", sub1.containsKey("skey1"));
		assertTrue("sub1.containstKey('skey2')", sub1.containsKey("skey2"));
		assertTrue("sub1.containstKey('sub2')", sub1.containsKey("sub2"));
		assertTrue("sub1.containstKey('sub3')", sub1.containsKey("sub3"));

		assertTrue("sub1.get('skey1') instanceof StringValue",
				sub1.get("skey1") instanceof StringValue);
		assertTrue("sub1.get('skey2') instanceof StringValue",
				sub1.get("skey2") instanceof StringValue);
		assertTrue("sub1.get('sub2') instanceof ComplexValue",
				sub1.get("sub2") instanceof StringValue);
		assertTrue("sub1.get('sub3') instanceof ComplexValue",
				sub1.get("sub3") instanceof ComplexValue);

		assertTrue("sub1.get('skey1').toString()", sub1.get("skey1").toString()
				.equals("svalue1"));
		assertTrue("sub1.get('skey2').toString()", sub1.get("skey2").toString()
				.equals("svalue2"));

		ComplexValue sub3 = (ComplexValue) sub1.get("sub3");
		assertNotNull(sub3);
		assertTrue("sub3.keyset.size()", sub3.keySet().size() == 1);
		assertTrue("sub3.get('sub4') instanceof ComplexValue",
				sub3.get("sub4") instanceof ComplexValue);

		ComplexValue sub4 = (ComplexValue) sub3.get("sub4");
		assertNotNull(sub4);
		assertTrue("sub4.keyset.size()", sub3.keySet().size() == 1);
		assertTrue("sub4.get('s4key') instanceof StringValue",
				sub4.get("s4key") instanceof StringValue);
		assertTrue("sub4.get('s4key').toString()", sub4.get("s4key").toString()
				.equals("s4value"));

		assertTrue("toString() == original",
				equals(stringTest3, value.toString()));
	}

	public void test4() {
		ComplexValue value = ValueFactory.createComplexValue("");
		assertNotNull(value);

		try {
			value.put("key1", "value1");
			fail("put with a no Value instance");
		} catch (Exception e) {
			assertTrue(
					"put with a no Value instance throws a IllegalArgumentException",
					e instanceof IllegalArgumentException);
		}

		value.put("key1", ValueFactory.createValue("value1"));
		value.put("key2", ValueFactory.createValue("value2"));
		value.put("key3", ValueFactory.createValue("value3"));

		assertTrue("keyset.size()", value.keySet().size() == 3);

		assertTrue("containstKey('key1')", value.containsKey("key1"));
		assertTrue("containstKey('key2')", value.containsKey("key2"));
		assertTrue("containstKey('key3')", value.containsKey("key3"));

		/*
		 * assertTrue("get('key1') instanceof StringValue",value.get("key1")
		 * instanceof StringValue);
		 * assertTrue("get('key2') instanceof StringValue",value.get("key2")
		 * instanceof StringValue);
		 * assertTrue("get('key3') instanceof StringValue",value.get("key3")
		 * instanceof StringValue);
		 */

		assertTrue("get('key1').toString()", value.get("key1").toString()
				.equals("value1"));
		assertTrue("get('key2').toString()", value.get("key2").toString()
				.equals("value2"));
		assertTrue("get('key3').toString()", value.get("key3").toString()
				.equals("value3"));

		assertTrue("toString() == original",
				equals(stringTest1, value.toString()));
	}

	public void test5() {
		ComplexValue value = ValueFactory.createComplexValue("");
		ComplexValue sub1 = ValueFactory.createComplexValue("");
		assertNotNull(value);
		assertNotNull(sub1);

		value.put("key1", ValueFactory.createValue("value1"));
		value.put("key2", ValueFactory.createValue("value2"));
		value.put("key3", ValueFactory.createValue("value3"));

		sub1.put("skey1", ValueFactory.createValue("svalue1"));
		sub1.put("skey2", ValueFactory.createValue("svalue2"));

		value.put("sub1", sub1);

		assertTrue("keyset.size()", value.keySet().size() == 4);

		assertTrue("containstKey('key1')", value.containsKey("key1"));
		assertTrue("containstKey('key2')", value.containsKey("key2"));
		assertTrue("containstKey('key3')", value.containsKey("key3"));
		assertTrue("containstKey('sub1')", value.containsKey("sub1"));

		assertTrue("get('key1') instanceof StringValue",
				value.get("key1") instanceof StringValue);
		assertTrue("get('key2') instanceof StringValue",
				value.get("key2") instanceof StringValue);
		assertTrue("get('key3') instanceof StringValue",
				value.get("key3") instanceof StringValue);
		assertTrue("get('sub1') instanceof ComplexValue",
				value.get("sub1") instanceof ComplexValue);

		assertTrue("get('key1').toString()", value.get("key1").toString()
				.equals("value1"));
		assertTrue("get('key2').toString()", value.get("key2").toString()
				.equals("value2"));
		assertTrue("get('key3').toString()", value.get("key3").toString()
				.equals("value3"));

		sub1 = (ComplexValue) value.get("sub1");
		assertNotNull(sub1);
		assertTrue("sub1.keyset.size()", sub1.keySet().size() == 2);
		assertTrue("sub1.containstKey('skey1')", sub1.containsKey("skey1"));
		assertTrue("sub1.containstKey('skey2')", sub1.containsKey("skey2"));

		assertTrue("sub1.get('skey1') instanceof StringValue",
				sub1.get("skey1") instanceof StringValue);
		assertTrue("sub1.get('skey2') instanceof StringValue",
				sub1.get("skey2") instanceof StringValue);

		assertTrue("sub1.get('skey1').toString()", sub1.get("skey1").toString()
				.equals("svalue1"));
		assertTrue("sub1.get('skey2').toString()", sub1.get("skey2").toString()
				.equals("svalue2"));

		assertTrue("toString() == original",
				equals(stringTest2, value.toString()));
	}

	public void test6() {
		ComplexValue value = ValueFactory.createComplexValue("");
		ComplexValue sub1 = ValueFactory.createComplexValue("");
		ComplexValue sub3 = ValueFactory.createComplexValue("");
		ComplexValue sub4 = ValueFactory.createComplexValue("");
		assertNotNull(value);
		assertNotNull(sub1);
		assertNotNull(sub3);
		assertNotNull(sub4);

		value.put("key1", ValueFactory.createValue("value1"));
		value.put("key2", ValueFactory.createValue("value2"));
		value.put("key3", ValueFactory.createValue("value3"));

		sub4.put("s4key", ValueFactory.createValue("s4value"));

		sub3.put("sub4", sub4);

		sub1.put("skey1", ValueFactory.createValue("svalue1"));
		sub1.put("skey2", ValueFactory.createValue("svalue2"));
		sub1.put("sub2", ValueFactory.createComplexValue(""));
		sub1.put("sub3", sub3);

		value.put("sub1", sub1);

		assertTrue("keyset.size()", value.keySet().size() == 4);

		assertTrue("containstKey('key1')", value.containsKey("key1"));
		assertTrue("containstKey('key2')", value.containsKey("key2"));
		assertTrue("containstKey('key3')", value.containsKey("key3"));
		assertTrue("containstKey('sub1')", value.containsKey("sub1"));
		assertFalse("not containstKey('kk')", value.containsKey("kk"));

		assertTrue("get('key1') instanceof StringValue",
				value.get("key1") instanceof StringValue);
		assertFalse("get('key1') not instanceof ComplexValue",
				value.get("key1") instanceof ComplexValue);
		assertTrue("get('key2') instanceof StringValue",
				value.get("key2") instanceof StringValue);
		assertFalse("get('key2') not instanceof ComplexValue",
				value.get("key2") instanceof ComplexValue);
		assertTrue("get('key3') instanceof StringValue",
				value.get("key3") instanceof StringValue);
		assertTrue("get('sub1') instanceof ComplexValue",
				value.get("sub1") instanceof ComplexValue);

		assertTrue("get('key1').toString()", value.get("key1").toString()
				.equals("value1"));
		assertTrue("get('key2').toString()", value.get("key2").toString()
				.equals("value2"));
		assertTrue("get('key3').toString()", value.get("key3").toString()
				.equals("value3"));

		sub1 = (ComplexValue) value.get("sub1");
		assertNotNull(sub1);
		assertTrue("sub1.keyset.size()", sub1.keySet().size() == 4);
		assertTrue("sub1.containstKey('skey1')", sub1.containsKey("skey1"));
		assertTrue("sub1.containstKey('skey2')", sub1.containsKey("skey2"));
		assertTrue("sub1.containstKey('sub2')", sub1.containsKey("sub2"));
		assertTrue("sub1.containstKey('sub3')", sub1.containsKey("sub3"));

		assertTrue("sub1.get('skey1') instanceof StringValue",
				sub1.get("skey1") instanceof StringValue);
		assertTrue("sub1.get('skey2') instanceof StringValue",
				sub1.get("skey2") instanceof StringValue);
		assertTrue("sub1.get('sub2') instanceof ComplexValue",
				sub1.get("sub2") instanceof StringValue);
		assertTrue("sub1.get('sub3') instanceof ComplexValue",
				sub1.get("sub3") instanceof ComplexValue);

		assertTrue("sub1.get('skey1').toString()", sub1.get("skey1").toString()
				.equals("svalue1"));
		assertTrue("sub1.get('skey2').toString()", sub1.get("skey2").toString()
				.equals("svalue2"));

		sub3 = (ComplexValue) sub1.get("sub3");
		assertNotNull(sub3);
		assertTrue("sub3.keyset.size()", sub3.keySet().size() == 1);
		assertTrue("sub3.get('sub4') instanceof ComplexValue",
				sub3.get("sub4") instanceof ComplexValue);

		sub4 = (ComplexValue) sub3.get("sub4");
		assertNotNull(sub4);
		assertTrue("sub4.keyset.size()", sub3.keySet().size() == 1);
		assertTrue("sub4.get('s4key') instanceof StringValue",
				sub4.get("s4key") instanceof StringValue);
		assertTrue("sub4.get('s4key').toString()", sub4.get("s4key").toString()
				.equals("s4value"));

		assertTrue("toString() == original",
				equals(stringTest3, value.toString()));
	}

	public void test7() {
		ComplexValue value = ValueFactory.createComplexValue(stringTest7);
		assertNotNull(value);

		assertTrue("keyset.size()", value.keySet().size() == 4);

		assertTrue("containstKey('key1')", value.containsKey("key1"));
		assertTrue("containstKey('key2')", value.containsKey("key2"));
		assertTrue("containstKey('key3')", value.containsKey("key3"));
		assertTrue("containstKey('key4')", value.containsKey("key4"));

		assertTrue("get('key1') instanceof IntValue",
				value.get("key1") instanceof IntValue);
		assertFalse("get('key1') not instanceof ComplexValue",
				value.get("key1") instanceof ComplexValue);
		assertTrue("get('key2') instanceof FloatValue",
				value.get("key2") instanceof DoubleValue);
		assertFalse("get('key2') not instanceof ComplexValue",
				value.get("key2") instanceof ComplexValue);
		assertTrue("get('key3') instanceof BooleanValue",
				value.get("key3") instanceof BooleanValue);
		assertTrue("get('key4') instanceof IntValue",
				value.get("key4") instanceof IntValue);

		assertTrue("toString() == original",
				equals(stringTest7, value.toString()));

	}

	public void test8() {
		ComplexValue value = ValueFactory.createComplexValue("");
		assertNotNull(value);

		value.put("key1", ValueFactory.createValue(1));
		value.put("key2", ValueFactory.createValue(2.2));
		value.put("key3", ValueFactory.createValue(true));
		try {
			value.put("key4",
					ValueFactory.createValueByType("4", Types.INTEGER));
		} catch (Exception e) {
			fail();
		}
		ComplexValue sub1 = ValueFactory.createComplexValue("");
		sub1.put("skey1", ValueFactory.createValue(false));
		value.put("sub1", sub1);

		assertTrue("keyset.size()", value.keySet().size() == 5);

		assertTrue("containstKey('key1')", value.containsKey("key1"));
		assertTrue("containstKey('key2')", value.containsKey("key2"));
		assertTrue("containstKey('key3')", value.containsKey("key3"));
		assertTrue("containstKey('key4')", value.containsKey("key4"));
		assertTrue("containstKey('sub1')", value.containsKey("sub1"));

		assertTrue("get('key1') instanceof IntValue",
				value.get("key1") instanceof IntValue);
		assertFalse("get('key1') not instanceof ComplexValue",
				value.get("key1") instanceof ComplexValue);
		assertTrue("get('key2') instanceof FloatValue",
				value.get("key2") instanceof DoubleValue);
		assertFalse("get('key2') not instanceof ComplexValue",
				value.get("key2") instanceof ComplexValue);
		assertTrue("get('key3') instanceof BooleanValue",
				value.get("key3") instanceof BooleanValue);
		assertTrue("get('key4') instanceof IntValue",
				value.get("key4") instanceof IntValue);
		assertTrue("get('sub1') instanceof ComplexValue",
				value.get("sub1") instanceof ComplexValue);

		sub1 = (ComplexValue) value.get("sub1");
		assertTrue("sub1.keyset.size()", sub1.keySet().size() == 1);

		assertTrue("sub1.containstKey('skey1')", sub1.containsKey("skey1"));

		assertTrue("sub1.get('skey1') instanceof BooleanValue",
				sub1.get("skey1") instanceof BooleanValue);

		assertTrue("toString() == original",
				equals(stringTest8, value.toString()));

	}

}
