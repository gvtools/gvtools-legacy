package com.hardcode.gdbms.engine.values;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ComplexValue extends StringValue implements Map {
	private LinkedHashMap mapValues =null;

	private static String indentString(int level) {
		if (level < 1) return "";
		char[] chars =new char[level*4];
		Arrays.fill(chars,' ');
		return String.copyValueOf(chars);
	}
	/**
	 * Construye un objeto ConplexValue con el parser
	 *
	 * @param text
	 */
	private ComplexValue(KXmlParser parser) {
		super();
		this.mapValues = new LinkedHashMap();
		try {
			this.parse(parser);
		} catch (Exception e) {
			e.printStackTrace();
			super.setValue(null);
		}		
	}

	/**
	 * Construye un objeto ConplexValue con el texto que se pasa como parametro
	 *
	 * @param text
	 */
	ComplexValue(String text) {
		super();
		this.mapValues = new LinkedHashMap();
		this.setValue(text);		
	}

	/**
	 * Creates a new ComplexValue object.
	 */
	ComplexValue() {
		super();
		this.mapValues = new LinkedHashMap();
	}
	
	private void parse() {
		String value = super.getValue();
		this.mapValues.clear();
		if (value == null || value.length() == 0) {
			return;
		}
		KXmlParser parser = new KXmlParser();
		try {
			parser.setInput(new StringReader(value));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.parse(parser);
		} catch (Exception e) {
			super.setValue(null);
		}
	}
	
	
		
	private void parse(KXmlParser parser) throws XmlPullParserException, IOException, ParseException {
		/*
		 * Se espera que la le va a llegar una cadena
		 * con el siguiente formato:
		 * 
		 * <dato1>valor1</dato1>
		 * <dato2>valor2</dato2>
		 * <dato3>
		 *     <dato3_1>valor3</dato3_1>
		 *     <dato3_2>
		 *         <dato3_2_1>valor4</dato3_2_1>
		 *         ....
		 *     </dato3_2>		      
		 * </dato3>
		 * ....
		 * 
		 * 
		 * 
		 * dentro del mapValues se registran los
		 * valores de 'dato1', 'dato2' y 'dato3'.
		 * Este ultimo sera a su vez un ComplexValue, y 
		 * asi recursivamente.
		 */
		
		//FIXME: OJO!!!! que hacemos con la excepciones		
		
		try {					
			String key;
			Value value;
			String cad;
			String type;
			if (parser.getEventType() == XmlPullParser.START_DOCUMENT) {
				parser.nextTag();
			}
			
			while (parser.getEventType() == XmlPullParser.START_TAG) {				
				key = parser.getName();
				try {
					type = parser.getAttributeValue(null,"_type");
					cad = parser.nextText();
					if (type != null) {
						value = ValueFactory.createValueByValueName(cad,type);
					} else {
						value = ValueFactory.createValue(cad);
					}
				} catch (XmlPullParserException e) {
					if (parser.getEventType() == XmlPullParser.START_TAG) {
						value = new ComplexValue(parser);
					} else if (parser.getEventType() == XmlPullParser.END_TAG) {
						continue;
					} else {
						throw e;
					}
				}
				parser.require(XmlPullParser.END_TAG, null, key);
				
				this.mapValues.put(key,value);

				parser.nextTag();				
			}
		} catch (XmlPullParserException e) {
			if (parser.getEventType() == XmlPullParser.END_DOCUMENT) return; 
			throw e;
		}
		
	}
	
	
	private String dump() {
		StringWriter buffer = new StringWriter();
		try {
			this.dumpToWriter(buffer,0);
		} catch (IOException e) {
			return null;
		}
		return buffer.toString();
	}
	
	private void dumpToWriter(Writer buffer, int indent) throws IOException {
		Iterator iter = this.mapValues.entrySet().iterator();		
		String identStr = indentString(indent);
		indent++;
		Entry entry;
		String key;
		String typeString;
		Value value;
		while (iter.hasNext()) {
			entry = (Entry)iter.next();
			key = (String)entry.getKey();
			value = (Value)entry.getValue();
			buffer.write(identStr+"<"+key+ getDumpTypePropertyString(value) +">");
			if (value instanceof ComplexValue) {
				buffer.write("\n");
				((ComplexValue)value).dumpToWriter(buffer,indent);
				buffer.write(identStr);
			} else {
				buffer.write(value.toString());
			}
			buffer.write("</"+key+">\n");
		}
		
	}
	
	private String getDumpTypePropertyString(Value value) {
		
		if (value instanceof StringValue) {
			return "";
		} else if (value instanceof ComplexValue) {
			return "";
		} else{
			String classname = value.getClass().getName();			
			return " _type=\"" + (classname.substring(classname.lastIndexOf(".")+1)) + "\"";
		}
		
	}

	public int size() {	
		return this.mapValues.size();
	}

	public void clear() {
		super.setValue("");
		this.mapValues.clear();

	}

	public boolean isEmpty() {		
		return this.mapValues.isEmpty();
	}

	public boolean containsKey(Object key) {		
		return this.mapValues.containsKey(key);
	}

	public boolean containsValue(Object value) {		
		return this.mapValues.containsValue(value);
	}

	public Collection values() {
		return this.mapValues.values();
	}

	public void putAll(Map t) {
		throw new UnsupportedOperationException();
	}

	public Set entrySet() {		
		return this.mapValues.entrySet();
	}

	public Set keySet() {
		return this.mapValues.keySet();
	}

	public Object get(Object key) {
		return this.mapValues.get(key);
	}

	public Object remove(Object key) {
		return this.mapValues.remove(key);
	}

	public Object put(Object key, Object value) {
		throw new IllegalArgumentException("'value' must be a Value instance");		
		//return this.mapValues.put(key,value);
	}
	
	public Object put(Object key, Value value) {
		return this.mapValues.put(key,value);
	}
	

	public String getStringValue(ValueWriter writer) {
		super.setValue(this.dump());
		return super.getStringValue(writer);
	}

	public String getValue() {
		super.setValue(this.dump());
		String val = super.getValue();
		if (val == null){
			return "";
		}
		return val;
	}

	public void setValue(String value) {		
		super.setValue(value);
		this.parse();
	}
	
	public String toString() {	
		return this.getValue();		
	}
	public int getSQLType() {		 
		//return super.getSQLType(); --> Types.LONGVARCHAR;
		return Types.STRUCT;
	}

}
