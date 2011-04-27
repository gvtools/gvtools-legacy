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
package org.gvsig.symbology.gui.layerproperties;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.ILabelable;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.operations.Cancel;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ILabelingStrategyPanel;

public class TestILabelingStrategyPanel extends TestCase {
	private static Hashtable<
		Class<? extends ILabelingStrategy>, 
		LabelingPanelTestCase> classesToTest;
	private static boolean init;
	
	
	public static void addPanelTest(LabelingPanelTestCase labelPanelTestClass) {
		if (classesToTest == null) {
			classesToTest = new Hashtable<
									Class<? extends ILabelingStrategy>, 
									LabelingPanelTestCase
								>();
			Class<? extends ILabelingStrategy> c = labelPanelTestClass.
				newInstance().
				getLabelingStrategyClass();
			if (!classesToTest.containsKey(c))
				classesToTest.put(c, labelPanelTestClass);
		}
		
	}

	public static ILabelingStrategyPanel[] getNewLabelingStrategyInstances() {
		if (init) suite();
		init = false;
		ArrayList<ILabelingStrategyPanel> stratPanels = new ArrayList<ILabelingStrategyPanel>();
		for (LabelingPanelTestCase p : classesToTest.values()) {
			stratPanels.add( p.newInstance() );
		}
		return stratPanels.toArray(new ILabelingStrategyPanel[0]);
	}

	public static String shortClassName(Class<?> c) {
		return c.getName().substring(
				c.getName().lastIndexOf('.')+1,
				c.getName().length());
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Integration test for labeling strategy configuration panels");
		suite.addTestSuite(TestILabelingStrategyPanel.class);
		addPanelTest(new GeneralLabelingStrategyPanelTestCase());
		return suite;
	}
	

	public void testGetLabelingStrategyName() {
		System.out
				.println("TestILabelingStrategyPanel.testGetLabelingStrategyName()"+": Not yet implemented!");
	}
	
	/**
	 * Tests the default settings of a labeling strategy panel.
	 * (Detects if the labeling strategy crashes when no user
	 * interaction has been made)
	 */
	public void testGetDefaultSettingsLabelingStrategy() {
		ILabelingStrategyPanel[] p = getNewLabelingStrategyInstances();
		
		for (int i = 0; i < p.length; i++) {
			LabelingPanelTestCase testCase = classesToTest.
								get(p[i].getLabelingStrategyClass());
			FLayer testLayer = testCase.getTestLayer();
			p[i].setModel(testLayer, null); // the null is the key of this test
			
			ILabelingStrategy strat = p[i].getLabelingStrategy();
			try {
				tryStrategy(testCase, testLayer, strat);
			} catch (Error e) {
				fail(e.getMessage());
			}
		}
	}

	private static void tryStrategy(LabelingPanelTestCase testCase, FLayer testLayer, ILabelingStrategy strat) {
		if (testLayer == null) 
			throw new Error("test layer is null! please check "+
					"case "+shortClassName(testCase.getClass())+
					".getTestLayer() method)");

		if (testLayer instanceof ILabelable) {
			try {
				// necessery rendering contexts previous settings
				ILabelable labelable = (ILabelable) testLayer;
				labelable.setLabelingStrategy(strat);
				MapContext mc = testCase.getTestMapContext();
				ViewPort viewPort = mc.getViewPort();
				viewPort.setExtent(testLayer.getFullExtent());
				viewPort.setImageSize(new Dimension(400, 400));
				BufferedImage bi = new BufferedImage(
						viewPort.getImageWidth(),
						viewPort.getImageHeight(),
						BufferedImage.TYPE_4BYTE_ABGR
				);
				
				// the actual test
				labelable.drawLabels(
						bi, 
						bi.createGraphics(),
						viewPort,
						new Cancel(),
						mc.getScaleView(),
						MapContext.getScreenDPI());
			} catch (ReadDriverException e) {
				throw new Error(e);
			} catch (Exception e) {
				fail("The rendering of the strategy failed. At this point, it can be due either to " +
					 "the panel malfunction, or to the LabelingStrategy itself. If the ILabelingStrategy "+
					 "'"+shortClassName(strat.getClass())+"' passed all the tests in FMap (TestILabelingStrategy), " +
					 "then the problem is in the panel, otherwise ensure that '"+shortClassName(strat.getClass())+"' " +
					 "passes such tests before testing its config panel ("+ shortClassName(testCase.panelClazz)+") to " +
					 "be sure that the problem is in the panel.");
			}
		} else {
			throw new Error("test layer is not labelable! " +
					"(this is a misconcept bug in the test " +
					"case "+shortClassName(testCase.getClass())+
					"'.getTestLayer() method)");
		}
	}

	public void testSetModel() {
		System.out.println("TestILabelingStrategyPanel.testSetModel()"+": Not yet implemented!");
	}
	
	public void testGetLabelingStrategyClass() {
		System.out
				.println("TestILabelingStrategyPanel.testGetLabelingStrategyClass()"+": Not yet implemented!");
	}
}
