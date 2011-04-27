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
package org.gvsig.symbology;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.gvsig.remoteClient.sld.symbolizerTests.TestSLDSymbolizers;
import org.gvsig.symbology.fmap.labeling.parse.TestLabelExpressionParser;
import org.gvsig.symbology.fmap.rendering.TestExpressionParser;
import org.gvsig.symbology.gui.layerproperties.TestILabelingStrategyPanel;
import org.gvsig.symbology.symbols.TestCartographicSupporForSymbol;
import org.gvsig.symbology.symbols.TestSymbols;

import com.iver.cit.gvsig.fmap.layers.LayerFactory;

/**
 * extSymbology
 * AllTests.java
 *
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jun 16, 2008
 *
 */
public class AllTests extends TestCase {

	private static File baseDataPath;
	private static File baseDriversPath;



	public static Test suite() {
		// NOTE: order is important for incremental testing

		
		TestSuite suite = new TestSuite("All tests in extSymbology");
		
		/* 1st-tier tests */
			/* SLD support tests */
			suite.addTest(TestSLDSymbolizers.suite());
		
		/* 2nd-tier tests */
			/* symbols sybsystem */
			suite.addTest(TestSymbols.suite());
			
			/* cartographic support subsystem */
			suite.addTestSuite(TestCartographicSupporForSymbol.class);
			
			/* filter expression language parser and runtime */
			suite.addTestSuite(TestExpressionParser.class);
			
			/* label expression language parser and runtime */
			suite.addTestSuite(TestLabelExpressionParser.class);
		
		/* 3rd-tier tests. the GUI */
			/* labeling panels */
			suite.addTest(TestILabelingStrategyPanel.suite());
			
			
		return suite;
	}
	
	

	public static void setUpDrivers() {
		try {
			baseDataPath = new File("src-test/test-data/layer-sample-files/");
			System.out.println(baseDataPath.getAbsolutePath());
			if (!baseDataPath.exists())
				throw new Exception("No se encuentra el directorio con datos de prueba");

			String fwAndamiDriverPath = com.iver.cit.gvsig.fmap.AllTests.fwAndamiDriverPath; 
			baseDriversPath = new File(fwAndamiDriverPath);
			if (!baseDriversPath.exists())
				throw new Exception("Can't find drivers path: " + fwAndamiDriverPath);

			LayerFactory.setDriversPath(baseDriversPath.getAbsolutePath());
			if (LayerFactory.getDM().getDriverNames().length < 1)
				throw new Exception("Can't find drivers in path: " + fwAndamiDriverPath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
