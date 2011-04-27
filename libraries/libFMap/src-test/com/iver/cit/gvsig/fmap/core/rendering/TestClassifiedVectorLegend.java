/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
package com.iver.cit.gvsig.fmap.core.rendering;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;

import junit.framework.TestCase;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.IDataSourceListener;
import com.hardcode.gdbms.engine.data.SourceInfo;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.TestISymbol;
import com.iver.cit.gvsig.fmap.rendering.IClassifiedVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.ILegend;

/**
 * <p>
 * This is the integration test for all the legends implementing
 * the interface IClassifiedVectorLegend.<br>
 * </p>
 * <p>
 * It ensures that the legend tested will behave as it is expected
 * within the app in all the places where symbol classification 
 * based on vector layers is required.<br>
 * </p>
 * <p>
 * It is linked to the types handled by the FLyrVect values 
 * </p>
 *  
 * TestClassifiedVectorLegend.java
 *
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jun 12, 2008
 *
 */
public class TestClassifiedVectorLegend extends TestCase{
	public static final Value v0 = (Value) ValueFactory.createValue(0);
	public static final Value v1 = (Value) ValueFactory.createValue(1);
	public static final Value v2 = (Value) ValueFactory.createValue(2);
	public static final Value v3 = (Value) ValueFactory.createValue(3);

	private IClassifiedVectorLegend[] legends;
	private Hashtable<Object, ISymbol> symTable;
	private ISymbol[] symbols = TestISymbol.getNewSymbolInstances();
	
	public static MockDataSource mockDataSource = new MockDataSource();
	/**
	 * To avoid duplicated validation logic in the test a mock object is created
	 * to use a DataSource for this test.
	 *
	 */
	static class MockDataSource implements DataSource {
		
		private static final String FIELD3 = "field3";
		private static final String FIELD2 = "field2";
		private static final String FIELD1 = "field1";
		private static final String FIELD0 = "field0";
		private static final int FIELDID = 0;

		private static final Value[] feature0Values = new Value[] { v0, v1, v2, v3, };
		private static final Value[] feature1Values = new Value[] { v3, v0, v1, v2, };
		private static final Value[] feature2Values = new Value[] { v2, v3, v0, v1, };
		private static final Value[] feature3Values = new Value[] { v1, v2, v3, v0, };

		public static final Value[][] featureValues = new Value[][] {
			feature0Values,
			feature1Values,
			feature2Values,
			feature3Values,};
		
		public  String[] fieldNames = new String[] {FIELD0,FIELD1,FIELD2,FIELD3,};
		
		
		public void start() throws ReadDriverException 								{ }
		public void stop() throws ReadDriverException 								{ }
		public long[] getWhereFilter() throws IOException 							{return null;}
		public DataSourceFactory getDataSourceFactory() 							{return null;}
		public Memento getMemento() throws MementoException 						{return null;}
		public void setDataSourceFactory(DataSourceFactory dsf) 					{ }
		public void setSourceInfo(SourceInfo sourceInfo)  							{ }
		public SourceInfo getSourceInfo() 											{return null;}
		public String getAsString() throws ReadDriverException 						{return null;}
		public void remove() throws WriteDriverException 							{ }
		public int[] getPrimaryKeys() throws ReadDriverException 					{return null;}
		public ValueCollection getPKValue(long rowIndex)throws ReadDriverException 	{return null;}
		public String getPKName(int fieldId) throws ReadDriverException 			{return null;}
		public String[] getPKNames() throws ReadDriverException 					{return null;}
		public int getPKType(int i) throws ReadDriverException 						{return 0;}
		public int getPKCardinality() throws ReadDriverException 					{return 0;}
		public Value[] getRow(long rowIndex) throws ReadDriverException 			{return null;}
		public DataWare getDataWare(int mode) throws ReadDriverException 			{return null;}
		public boolean isVirtualField(int fieldId) throws ReadDriverException 		{return false;}
		public Driver getDriver() 													{return null;}
		public void reload() throws ReloadDriverException 							{ }
		public void addDataSourceListener(IDataSourceListener listener) 			{ }
		public void removeDataSourceListener(IDataSourceListener listener) 			{ }
		public Value getFieldValue(long rowIndex, int fieldId)throws ReadDriverException {return null;}
		public int getFieldCount() throws ReadDriverException 						{return 0;}
		public long getRowCount() throws ReadDriverException 						{return 0;}
		public int getFieldWidth(int i) throws ReadDriverException 					{return 0;}

		public String getFieldName(int fieldId) throws ReadDriverException {
			return fieldNames[fieldId];
		}

		public String getName(){
			return "Mock datasource used for testing only";
		}

		public String[] getFieldNames() throws ReadDriverException {
			return fieldNames;
		}

		public int getFieldIndexByName(String fieldName)throws ReadDriverException {
			for (int i = 0; i < fieldNames.length; i++) {
				if (fieldNames[i].equals(fieldName))
					return i;
			}
			return -1;
		}

		public int getFieldType(int i) throws ReadDriverException {
			return Types.INTEGER;
		}

	}

	protected void setUp() throws Exception {
		super.setUp();
		ILegend[] allLegends = TestILegend.getNewLegendInstances();
		// Filter the marker ones
		ArrayList<IClassifiedVectorLegend> legends = new ArrayList<IClassifiedVectorLegend>();

		for (int i = 0; i < allLegends.length; i++) {
			if (allLegends[i] instanceof IClassifiedVectorLegend) {
				legends.add((IClassifiedVectorLegend) allLegends[i]);

			}
		}
		this.legends = (IClassifiedVectorLegend[]) legends.toArray(new IClassifiedVectorLegend[legends.size()]);
	}
	/**
	 * ensures clear is performed correctly
	 */
	public void testClear() {
		System.out.println("TestClassifiedVectorLegend.testClear()");
		System.out.println("\t not yet implemented");
	}

	public void testGetClassifyingFieldNames() {
		System.out
				.println("TestClassifiedVectorLegend.testGetClassifyingFieldNames()");
		System.out.println("\t not yet implemented");
	}

	public void testSetClassifyingFieldNames() {
		System.out
				.println("TestClassifiedVectorLegend.testSetClassifyingFieldNames()");
		System.out.println("\t not yet implemented");
	}

	public void testAddSymbol() {
		System.out.println("TestClassifiedVectorLegend.testAddSymbol()");
		System.out.println("\t not yet implemented");
		
	}

	
	public void testDelSymbol() {
		System.out.println("TestClassifiedVectorLegend.testDelSymbol()");
		System.out.println("\t not yet implemented");
	}

	public void testReplace() {
		System.out.println("TestClassifiedVectorLegend.testReplace()");
		System.out.println("\t not yet implemented");
	}

	public void testGetClassifyingFieldTypes() {
		System.out
				.println("TestClassifiedVectorLegend.testGetClassifyingFieldTypes()");
		System.out.println("\t not yet implemented");
	}
	
	public void testSetClassifyingFieldTypes() {
		System.out
				.println("TestClassifiedVectorLegend.testSetClassifyingFieldTypes()");
		System.out.println("\t not yet implemented");
	}
	
	
	public void testClearFiresContentsChanged() {
		System.out.println("TestClassifiedVectorLegend.testClear()");
		System.out.println("\t not yet implemented");
	}

	public void testGetClassifyingFieldNamesFiresContentsChanged() {
		System.out
				.println("TestClassifiedVectorLegend.testGetClassifyingFieldNames()");
		System.out.println("\t not yet implemented");
	}

	public void testSetClassifyingFieldNamesFiresContentsChanged() {
		System.out
				.println("TestClassifiedVectorLegend.testSetClassifyingFieldNames()");
		System.out.println("\t not yet implemented");
	}

	public void testAddSymbolFiresContentsChanged() {
		System.out.println("TestClassifiedVectorLegend.testAddSymbol()");
		System.out.println("\t not yet implemented");
		
	}

	
	public void testDelSymbolFiresContentsChanged() {
		System.out.println("TestClassifiedVectorLegend.testDelSymbol()");
		System.out.println("\t not yet implemented");
	}

	public void testReplaceFiresContentsChanged() {
		System.out.println("TestClassifiedVectorLegend.testReplace()");
		System.out.println("\t not yet implemented");
	}

	public void testGetClassifyingFieldTypesFiresContentsChanged() {
		System.out
				.println("TestClassifiedVectorLegend.testGetClassifyingFieldTypes()");
		System.out.println("\t not yet implemented");
	}
	
	public void testSetClassifyingFieldTypesFiresContentsChanged() {
		System.out
				.println("TestClassifiedVectorLegend.testSetClassifyingFieldTypes()");
		System.out.println("\t not yet implemented");
	}
	/**
	 * Compares the classification amount of elements with symbols. With this
	 * it should be sure that the legend is correctly shown when adding it 
	 * to a layout as the list of symbols, at the TOC, etc... or in 
	 */
	public void testICLAdittion() {

		// Fills the legend
		for (int i = 0; i < legends.length; i++) {
			Object[] sampleValues = TestILegend.getLegendTestCaseByLegend(legends[i]).getTestSampleValues();
			fillClassifiedLegend(legends[i], sampleValues);
			assertEquals(legends[i].getClassName()
					+ " fails with the comparation of the number of symbols",
					legends[i].getSymbols().length,
					sampleValues.length);
		}
	}
	
	/**
	 * This method is used to add symbols to a legend.That is, it takes an array
	 * of IClassifiedVectorialLegend which is empty andm, using a second array
	 * of objects (values), the first one is filled.Also, a hash table is filled
	 * too using the array of objects (it will be useful in some tests to check
	 * that a symbol can be taken using a feature) .
	 *
	 * @param legend
	 * @return
	 */
	private void fillClassifiedLegend(IClassifiedVectorLegend legend,
			Object[] values) {
		// initialize the hash table
		symTable = new Hashtable<Object, ISymbol>();

		// to add symbols to the legend and the hash table
		for (int j = 0; j < values.length; j++) {

			ISymbol sym = symbols[j % symbols.length];
			legend.addSymbol(values[j], sym);
			symTable.put(values[j], sym);
		}
	}

}
