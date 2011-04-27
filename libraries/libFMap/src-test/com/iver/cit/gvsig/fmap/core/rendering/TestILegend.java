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
package com.iver.cit.gvsig.fmap.core.rendering;

import java.lang.reflect.Field;
import java.util.ArrayList;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendClearEvent;
import com.iver.cit.gvsig.fmap.rendering.LegendContentsChangedListener;
import com.iver.cit.gvsig.fmap.rendering.SymbolLegendEvent;
import com.iver.utiles.XMLEntity;

/**
 * <p>
 * Integration test for all legends. It tests basic operations:
 * </p>
 * <ol>
 * 	<li>legend clonation (used in the layer properties window)</li>
 * 	<li>legend knows how to self-define correctly in the XMLEntity</li>
 *  <li>legend registers and manages all the listeners for its contents changes</li>
 *  
 * </ol>
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class TestILegend extends TestCase {
	private static boolean init = true;
	private static ArrayList<AbstractLegendTestCase> classesToTest;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Integration test for "+ILegend.class.getCanonicalName());
	    suite.addTestSuite(TestILegend.class);
	    suite.addTestSuite(TestVectorLegend.class);
	    suite.addTestSuite(TestIClassifiedLegend.class);
	    
	    suite.addTestSuite(TestIntervalLegend.class);
	    suite.addTestSuite(TestClassifiedVectorLegend.class);
	    
		addLegendToTest(new SingleSymbolLegendTest());
		addLegendToTest(new VectorialUniqueValueLegendTest());
		addLegendToTest(new VectorialIntervalLegendTest());
		return suite;
	}

	public static void addLegendToTest(AbstractLegendTestCase legendTestClass) {
		if (classesToTest == null) 
			classesToTest = new ArrayList<AbstractLegendTestCase>();
		if (!classesToTest.contains(legendTestClass))
			classesToTest.add(legendTestClass);
	}
	
	public static AbstractLegendTestCase getLegendTestCaseByLegend(ILegend leg) {
		for (AbstractLegendTestCase testCase : classesToTest) {
			if (testCase.legClazz.equals(leg.getClass())) {
				return testCase;
			}
		}
		return null;
	}
	
	public static String shortClassName(Class<?> c) {
		return c.getName().substring(
				c.getName().lastIndexOf('.')+1,
				c.getName().length());
	}
	
 	public static String getNameForLegend(ILegend testLegend) {
		 return shortClassName(testLegend.getClass());
	}

	public static ILegend[] getNewLegendInstances() {
    	if (init ) suite();
    	init = false;
    	ILegend[] legends = new ILegend[classesToTest.size()];
        for (int i = 0; i < legends.length; i++) {
        	legends[i] = classesToTest.get(i).newInstance();
        }
        return legends;
    }

	/**
	 * this test ensures that the legend is self-defining. Checks that
	 * the legends contained by it can be replicated, and the rules for
	 * such legends as well.
	 * @throws XMLException
	 */
	public void testILegendSelfDefinition() throws XMLException{
		ILegend[] legends = getNewLegendInstances();
		for (int i = 0; i < legends.length; i++) {
			final ILegend theLegend = legends[i];
			final ILegend cloneLegend = theLegend.cloneLegend();
			assertTrue(theLegend.getClassName()+ " wrong class name declaration in getXMLEntity() ",
					cloneLegend.getClass().equals(theLegend.getClass()));
			final Field[] theLegendFields = theLegend.getClass().getFields();
			for (int j = 0; j < theLegendFields.length; j++) {
				final Field fi = theLegendFields[j];
				final boolean wasAccessible = fi.isAccessible();
				fi.setAccessible(true);

				try {
					assertTrue(theLegend.getClassName() + " fails or misses clonning the field " +fi.getName(),
							fi.get(theLegend).equals(fi.get(cloneLegend)));
				} catch (IllegalArgumentException e) {
					fail();
				} catch (IllegalAccessException e) {
					fail();
				}
				fi.setAccessible(wasAccessible);
			}
		}
	}
	
	public void testILegendClone() throws XMLException{
		ILegend[] legends = getNewLegendInstances();
		for (int i = 0; i < legends.length; i++) {
			final ILegend theLegend = legends[i];
			final ILegend cloneLegend =theLegend.cloneLegend();
			
			XMLEntity oriXML = theLegend.getXMLEntity();
			XMLEntity clonXML = cloneLegend.getXMLEntity();
			assertTrue(oriXML.hash() == clonXML.hash());
		}
	}

	class MockLegendContentsChangedListener implements LegendContentsChangedListener {
		public boolean legendClearedEventFired = false;
		public boolean symbolChangedEventFired = false;
		
		public void legendCleared(LegendClearEvent event) {
			legendClearedEventFired = true;
		}

		public boolean symbolChanged(SymbolLegendEvent e) {
			symbolChangedEventFired = true;
			return true;
		}
	}
	
	public void testLegendContentsChangedListener(LegendContentsChangedListener listener) {
		ILegend[] legends = getNewLegendInstances();
		for (int i = 0; i < legends.length; i++) {
			MockLegendContentsChangedListener mockLegendListener = new MockLegendContentsChangedListener();		
			MockLegendContentsChangedListener mockLegendListener2 = new MockLegendContentsChangedListener();
			
			legends[i].addLegendListener(mockLegendListener);
			legends[i].fireDefaultSymbolChangedEvent(new SymbolLegendEvent(null, null));
			assertTrue("fireDefaultSymbolChangedEvent does not " +
					"notify to the listeners in legend '"+
					getNameForLegend(legends[i])+"'", mockLegendListener.legendClearedEventFired);

			assertTrue("adding LegendContentsChangeListeners does " +
					"not add any listener, or getListeners() is no " +
					"returning all the linsteners installed in the " +
					"legend '"+getNameForLegend(legends[i]), legends[i].
					getListeners().length == 1 &&  legends[i].getListeners()[0].
					equals(mockLegendListener));
			
			legends[i].addLegendListener(mockLegendListener);
			assertTrue("adding LegendContentsChangeListeners does " +
					"not add any listener, or getListeners() is no " +
					"returning all the linsteners installed in the " +
					"legend '"+getNameForLegend(legends[i]), legends[i].
					getListeners().length == 2 &&  legends[i].getListeners()[1].
					equals(mockLegendListener2));
			
			
			legends[i].removeLegendListener(mockLegendListener2);
			
			assertTrue("removing LegendContentsChangeListeners does " +
					"not removes any listener, or getListeners() is no " +
					"returning all the linsteners installed in the " +
					"legend '"+getNameForLegend(legends[i]), legends[i].
					getListeners().length == 1 &&  legends[i].getListeners().
					equals(mockLegendListener));
			
			mockLegendListener.symbolChangedEventFired =false;
			legends[i].fireDefaultSymbolChangedEvent(new SymbolLegendEvent(null, null));
			assertFalse("removeLegendListener does not " +
					"remove to the listeners in legend '"+
					getNameForLegend(legends[i])+"'", mockLegendListener.legendClearedEventFired);
		}
	}
	
	/**
	 * this test ensures that any legend always have a legend ready to be used.
	 * an empty legend is incorrect.
	 *
	 */
	public void testSymbolAvailability() {
		ILegend[] legends = getNewLegendInstances();
		for (int i = 0; i < legends.length; i++) {
			assertNotNull("Legend no. "+i+" '"+legends[i].getClassName()+" does not have a legend ready to be used", legends[i].getDefaultSymbol());
		}

		for (int i = 0; i < legends.length; i++) {

			if (legends[i] instanceof IVectorLegend) {
				IVectorLegend vectLegend = (IVectorLegend) legends[i];
				try {
					vectLegend.setDefaultSymbol(null);
					fail("setDefaultSymbol(ISymbol) should not accept null values");
				} catch (NullPointerException e) {
					// correct
				}
			}

		}
	}


}
