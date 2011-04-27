package com.iver.cit.jdwglib;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.drivers.dwg.DwgMemoryDriver;
/*
 * Created on 15-mar-2007
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: DwgRegionOfInterestTestCase.java 12374 2007-06-27 15:22:38Z azabala $
 * $Log$
 * Revision 1.2  2007-06-27 15:22:38  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/23 18:06:07  azabala
 * *** empty log message ***
 *
 *
 */
 
public class DwgRegionOfInterestTestCase extends TestCase {
	private File baseDataPath;

	protected void setUp() throws Exception {
		super.setUp();
		URL url = this.getClass().getResource("DwgFileTest_data");
		if (url == null)
			throw new Exception("Can't find 'DwgFileTest_data' dir");

		baseDataPath = new File(url.getFile());
		if (!baseDataPath.exists())
			throw new Exception("Can't find 'DwgFileTest_data' dir");

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void test1() throws IOException, DriverException, ReadDriverException{
		DwgMemoryDriver driver = new DwgMemoryDriver();
		String fileName = baseDataPath.
			getAbsolutePath() + "/TORRE03.DWG";
		File file = new File(fileName);
		driver.open(file);
		driver.initialize();
		long withRoi = driver.getRowCount();
		
		driver.initialize();
		driver.open(file);
		long afterRoi = driver.getRowCount();
		assertFalse(withRoi == afterRoi);
	}
	
	

}

