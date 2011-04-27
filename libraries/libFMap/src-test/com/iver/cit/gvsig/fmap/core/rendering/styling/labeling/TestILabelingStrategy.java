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

import java.util.ArrayList;

import junit.framework.TestCase;

import com.hardcode.gdbms.engine.instruction.FieldNotFoundException;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.AttrInTableLabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;

public class TestILabelingStrategy extends TestCase {
	private static ArrayList<Class <? extends ILabelingStrategy>> classesToTest;
	
	
	@SuppressWarnings("unchecked")
	private static ArrayList<Class <? extends ILabelingStrategy>> getClassesToTest() {
		if (classesToTest == null) {
			classesToTest = new ArrayList ();

			TestILabelingStrategy.addLabelingStrategyToTest(AttrInTableLabelingStrategy.class);
		}

		return classesToTest;
	}

	/**
	 * The main purpose of this method is to create new strategies instances to be used for
	 * other test strategies. 
	 * @return
	 * @throws FieldNotFoundException
	 */
	public static ILabelingStrategy[] getNewStrategiesInstances() throws FieldNotFoundException{
		ILabelingStrategy[] strategies = new ILabelingStrategy[getClassesToTest().size()];
		for (int i = 0; i < strategies.length; i++) {

			try {
				strategies[i] = (ILabelingStrategy) (getClassesToTest().get(i)).newInstance();
//				fillthelegend(strategys[i]);

			} catch (InstantiationException e) {
				fail("Instantiating class");
			} catch (IllegalAccessException e) {
				fail("Class not instantiable");
			}

		}
		return strategies;
	}


	@SuppressWarnings("unchecked")
	public static void addLabelingStrategyToTest(
			Class<? extends ILabelingStrategy> labelingStrategyClass) {
		try {
			ILabelingStrategy strategy = (ILabelingStrategy) labelingStrategyClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			fail("Instantiating class, cannot test a non-instantiable labeling strategy");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			fail("Class not instantiable");
		} catch (ClassCastException ccEx) {
			fail("Cannot test a non labeling strategy class");
		}
		getClassesToTest().add(labelingStrategyClass);
	}

	/**
	 * Tests the draw of each of the strategies installed in the tests against 
	 * each of the labeling methods installed in the tests.
	 * 
	 * @throws Exception
	 */
	public void testAllLabelingMethods() throws Exception {
		ILabelingStrategy[] strats = getNewStrategiesInstances();
		for (int i = 0; i < strats.length; i++) {
			ILabelingStrategy testStrategy = strats[i];
			ILabelingMethod[] methods = TestILabelingMethod.getNewMethodInstances();
			for (int j = 0; j < methods.length; j++) {
				ILabelingMethod testMethod = methods[j];
				
			}
		}
		
	}
	
	
//	/**
//	 * Returns the labeling strategy currently in use. The labeling strategy handles
//	 * a list of LabelClass that allows to handle several definition of labels
//	 * in the layer.
//	 * @return ILabelingMethod, the current one.
//	 * @see ILabelingMethod
//	 */
//	public ILabelingMethod getLabelingMethod();
//
//	/**
//	 * Sets the labeling strategy that will be used the next time the the draw is invoked.
//	 * @param   strategy, the new labeling strategy
//	 */
//	public void setLabelingMethod(ILabelingMethod strategy);
//
//
//	/**
//	 * Returns the current placement constraints that determine the position
//	 * where the label is placed.
//	 * @return
//	 */
//	public IPlacementConstraints getPlacementConstraints();
//
//	/**
//	 * Sets the PlacementConstraints that will determine where to place the labels. The change will take effect next time the draw(...) strategy is invoked.
//	 * @param  constraints
//	 */
//	public void setPlacementConstraints(IPlacementConstraints constraints);
//
//	/**
//	 * Returns the current placement constraints that determine the position
//	 * where the label is placed.
//	 * @return
//	 */
//	public IZoomConstraints getZoomConstraints();
//
//	/**
//	 * Sets the PlacementConstraints that will determine where to place the labels. The change will take effect next time the draw(...) strategy is invoked.
//	 * @param  constraints
//	 */
//	public void setZoomConstraints(IZoomConstraints constraints);
//
//	/** Causes the labels to be drawn. The policy of process is determined by
//	 * the LabelingStrategy previously set.
//	 *
//	 * @param mapImage
//	 * @param mapGraphics
//	 * @param viewPort
//	 * @param cancel
//	 * @param dpi TODO
//	 * @throws DriverException
//	 */
//	public void draw(BufferedImage mapImage, Graphics2D mapGraphics, ViewPort viewPort,
//			Cancellable cancel, double dpi) throws ReadDriverException;
//
//	/**
//	 * Applies the printer properties to the rendering process to match its attributes.
//	 * The result is manifested in the Graphics2D g which is the object sent to the printer.
//	 * @param g
//	 * @param viewPort
//	 * @param cancel
//	 * @param properties
//	 * @throws ReadDriverException
//	 */
//	public void print(Graphics2D g, ViewPort viewPort, Cancellable cancel, PrintRequestAttributeSet properties)
//	throws ReadDriverException;
//
//	/**
//	 * Returns a non-null String[] containing the names of the fields involved in the
//	 * labeling. If this strategy contains more than one LabelClass the result is an
//	 * array with all the names of the fields used by all the LabelClass, with no duplicates.
//	 * @return
//	 */
//	public String[] getUsedFields();
//
//	public void setLayer(FLayer layer) throws ReadDriverException;
//	
//	public boolean shouldDrawLabels(double scale);

}
