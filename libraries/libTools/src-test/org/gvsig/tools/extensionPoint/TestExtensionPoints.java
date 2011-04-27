/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Gobernment (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 */
package org.gvsig.tools.extensionPoint;

import java.util.Iterator;

import junit.framework.TestCase;

public class TestExtensionPoints extends TestCase {

	protected void setUp() throws Exception {
	    // Remove previous registered extension points, so other tests
        // don't affect the test validations.
        ExtensionPointsSingleton.getInstance().clear();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void test() {
		ExtensionPoints extensionPoints = new ExtensionPoints();
		
		extensionPoints.add("LayerWizars","WMS",UnaExtensionDePrueba1.class);
		extensionPoints.add("LayerWizars","WCS",UnaExtensionDePrueba2.class);
		extensionPoints.add("OtherWizars","uno",UnaExtensionDePrueba1.class);
		extensionPoints.add("OtherWizars","dos",UnaExtensionDePrueba2.class);
		
		// Comprobamos que el orden de las extensiones es el que hemos fijado.
		ExtensionPoint x = (ExtensionPoint)extensionPoints.get("LayerWizars");
		Iterator i =x.keySet().iterator();
		String[] nombres = {"WMS", "WCS" };
		int n = 0;
		while( i.hasNext() ) {
			String nombre = (String)i.next();
			assertTrue(nombres[n].equals(nombre));
			n++;	
		}
				
		
		ExtensionPoint extensionPointLayerWizars;
		extensionPointLayerWizars = (ExtensionPoint)extensionPoints.get("LayerWizars");
		assertTrue(extensionPointLayerWizars.containsKey("WMS"));
		assertTrue(extensionPointLayerWizars.containsKey("WCS"));
		
		assertEquals(extensionPoints.size(),2);
		assertEquals(extensionPointLayerWizars.size(),2);
		
		ExtensionPoint extensionPointLayerWizars2 = new ExtensionPoint("LayerWizars");
		
		
		extensionPointLayerWizars2.put("File",UnaExtensionDePrueba3.class);
		extensionPointLayerWizars2.put("JDBC",UnaExtensionDePrueba4.class);
		
		extensionPoints.put("LayerWizars",extensionPointLayerWizars2);
		
		extensionPointLayerWizars = (ExtensionPoint)extensionPoints.get("LayerWizars");
		assertEquals(extensionPoints.size(),2);
		assertEquals(extensionPointLayerWizars.size(),4);
		assertEquals(((ExtensionPoint)extensionPoints.get("OtherWizars")).size(),2);
		
		assertTrue(extensionPointLayerWizars.containsKey("WMS"));
		assertTrue(extensionPointLayerWizars.containsKey("WCS"));
		assertTrue(extensionPointLayerWizars.containsKey("File"));
		assertTrue(extensionPointLayerWizars.containsKey("JDBC"));
		
		assertEquals((extensionPointLayerWizars.get("JDBC")),UnaExtensionDePrueba4.class);
		assertEquals((extensionPointLayerWizars.get("WMS")),UnaExtensionDePrueba1.class);
		
		assertEquals(((ExtensionPoint)extensionPoints.get("OtherWizars")).get("uno"),UnaExtensionDePrueba1.class);
		
		ExtensionPoint extensionPointOther2 = new ExtensionPoint("OtherWizars");
		extensionPointOther2.put("tres",UnaExtensionDePrueba3.class);
		extensionPointOther2.put("cuatro",UnaExtensionDePrueba4.class);
		
		extensionPoints.put(extensionPointOther2);
		
		ExtensionPoint extensionPointOther = (ExtensionPoint)extensionPoints.get("OtherWizars");
		assertEquals(extensionPoints.size(),2);
		assertEquals(extensionPointLayerWizars.size(),4);
		assertEquals(extensionPointOther.size(),4);
		
		assertTrue(extensionPointOther.containsKey("uno"));
		assertTrue(extensionPointOther.containsKey("dos"));
		assertTrue(extensionPointOther.containsKey("tres"));
		assertTrue(extensionPointOther.containsKey("cuatro"));
		
		assertEquals((extensionPointOther.get("tres")),UnaExtensionDePrueba3.class);
		assertEquals((extensionPointOther.get("dos")),UnaExtensionDePrueba2.class);
		
		assertEquals(extensionPoints.get("Ninguno"),null);
	}

	public void testSingleton() {
		ExtensionPoints extensionPoints1 = ExtensionPointsSingleton.getInstance();

		extensionPoints1.add("LayerWizars","WMS",UnaExtensionDePrueba1.class);
		extensionPoints1.add("LayerWizars","WCS",UnaExtensionDePrueba2.class);
		extensionPoints1.add("OtherWizars","uno",UnaExtensionDePrueba1.class);
		extensionPoints1.add("OtherWizars","dos",UnaExtensionDePrueba2.class);
		
		ExtensionPoints extensionPoints2 = ExtensionPointsSingleton.getInstance();
		assertEquals(2, extensionPoints2.size());

		ExtensionPoint extensionPointLayerWizars;
		extensionPointLayerWizars = (ExtensionPoint)extensionPoints2.get("LayerWizars");
		assertTrue(extensionPointLayerWizars.containsKey("WMS"));
		assertTrue(extensionPointLayerWizars.containsKey("WCS"));
	}
}

class UnaExtensionDePrueba1 {
    public UnaExtensionDePrueba1() {
		;
	}
}
class UnaExtensionDePrueba2 {
	public UnaExtensionDePrueba2() {
	}
}

class UnaExtensionDePrueba3 {
	public UnaExtensionDePrueba3() {
	}
}

class UnaExtensionDePrueba4 {
	public UnaExtensionDePrueba4() {
	}
}