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
package com.iver.cit.gvsig.fmap.drivers.dgn;

import java.io.File;

import junit.framework.TestCase;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;

public class TestDgn extends TestCase {
	DgnMemoryDriver driver = new DgnMemoryDriver();
	File file = new File("c:/LP01C.dgn");

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestDgn.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * Test method for 'com.iver.cit.gvsig.fmap.drivers.dgn.DgnMemoryDriver.initialize()'
	 */
	public void testInitialize() {
		try {
			driver.open(file);
			driver.initialize();
			DGNReader reader = new DGNReader(file.getAbsolutePath());
				reader.DGNGotoElement(100);
				DGNElemCore elemento = reader.DGNReadElement();

				reader.DGNDumpElement(reader.getInfo(), elemento, null);
		}  catch (OpenDriverException e) {
			e.printStackTrace();
		} catch (InitializeDriverException e) {
			e.printStackTrace();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}

	}

}


