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
package com.iver.cit.gvsig.fmap.core.rendering.styling.labeling;

import java.lang.reflect.Field;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.hardcode.gdbms.engine.instruction.FieldNotFoundException;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.DefaultLabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;

public class TestILabelingMethod extends TestCase {
	private static ArrayList<? extends ILabelingMethod> classesToTest;
	transient private ILabelingMethod[] methods;

	
	@SuppressWarnings("unchecked")
	private static ArrayList getClassesToTest() {
		if (classesToTest == null) {
			classesToTest = new ArrayList ();

			TestILabelingMethod.addLabelingMethodToTest(DefaultLabelingMethod.class);
		}

		return classesToTest;
	}

	/**
	 * The main purpose of this method is to create new methods instances to be used for
	 * other test methods.
	 * @return
	 * @throws FieldNotFoundException
	 */
	public static ILabelingMethod[] getNewMethodInstances() throws FieldNotFoundException{
		ILabelingMethod[] methods = new ILabelingMethod[getClassesToTest().size()];
		for (int i = 0; i < methods.length; i++) {

			try {
				methods[i] = (ILabelingMethod) ((Class) getClassesToTest().get(i)).newInstance();

			} catch (InstantiationException e) {
				fail("Instantiating class");
			} catch (IllegalAccessException e) {
				fail("Class not instantiable");
			}

		}
		return methods;
	}


	protected void setUp() throws Exception {
		methods = getNewMethodInstances();
	}
	

	@SuppressWarnings("unchecked")
	public static void addLabelingMethodToTest(Class<? extends ILabelingMethod> labelingMethodClass) {
		try {
			ILabelingMethod method = (ILabelingMethod) labelingMethodClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			fail("Instantiating class, cannot test a non-instantiable labeling method");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			fail("Class not instantiable");
		} catch (ClassCastException ccEx) {
			fail("Cannot test a non labeling method class");
		}
		getClassesToTest().add(labelingMethodClass);
	}

	/**
	 * this test ensures that the method is self-defining. Checks that
	 * the labeling methods contained by it can be replicated, and the rules for
	 * such labeling methods as well.
	 * @throws XMLException
	 */
	public void testILabelingSelfDefinition() throws XMLException{
		for (int i = 0; i < methods.length; i++) {
			final ILabelingMethod theMethod = methods[i];
			final ILabelingMethod cloneMethod = theMethod.cloneMethod();
			assertTrue(theMethod.getClassName()+ " wrong class name declaration in getXMLEntity() ",
					cloneMethod.getClass().equals(theMethod.getClass()));
			final Field[] theLegendFields = theMethod.getClass().getFields();
			for (int j = 0; j < theLegendFields.length; j++) {
				final Field fi = theLegendFields[j];
				final boolean wasAccessible = fi.isAccessible();
				fi.setAccessible(true);

				try {
					assertTrue(theMethod.getClassName() + " fails or misses clonning the field " +fi.getName(),
							fi.get(theMethod).equals(fi.get(cloneMethod)));
				} catch (IllegalArgumentException e) {
					fail();
				} catch (IllegalAccessException e) {
					fail();
				}
				fi.setAccessible(wasAccessible);
			}
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Integration test for ILabelingMethod's.");
		
		
		return suite;
	}

}
