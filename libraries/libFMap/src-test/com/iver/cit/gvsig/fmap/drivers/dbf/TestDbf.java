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

package com.iver.cit.gvsig.fmap.drivers.dbf;

import java.io.File;

import junit.framework.TestCase;

import com.hardcode.gdbms.driver.exceptions.OpenDriverException;

public class TestDbf extends TestCase {
	DBFDriver driver = new DBFDriver();
	File file = new File("c:/prueba.dbf");

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestDbf.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * Test method for
	 * 'com.iver.cit.gvsig.fmap.drivers.dbf.DBFDriver.initialize()'
	 */
	public void testInitialize() {
		try {
			driver.open(file);
			System.out.println(driver.getFields());// FieldValue(1,0));
		} catch (OpenDriverException e) {
			e.printStackTrace();
		}

	}

}
