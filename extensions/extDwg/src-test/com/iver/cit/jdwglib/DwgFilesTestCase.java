/*
 * Created on 11-jun-2007
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
* $Id: DwgFilesTestCase.java 17118 2007-12-04 12:56:38Z vcaballero $
* $Log$
* Revision 1.1  2007-06-27 15:21:03  azabala
* first version in cvs
*
*
*/
package com.iver.cit.jdwglib;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import junit.framework.TestCase;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.drivers.dwg.DwgMemoryDriver;

/**
 * Multiple test with dwg files of many versions (DWG 12, 13-14, 2000)
 * @author azabala
 *
 */
public class DwgFilesTestCase extends TestCase {

	private File baseDataPath;
	boolean TEST_ALL = true;

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
		baseDataPath = null;
	}

	public void test1(){
		if(TEST_ALL){
			File[] directories = baseDataPath.listFiles();
			for(int j = 0; j < directories.length; j++){
				System.out.println("Probando dwg versión "+directories[j].getName());
				File[] files = directories[j].listFiles(new FilenameFilter(){
					public boolean accept(File arg0, String fileName) {
						if(fileName.endsWith("dwg")){
							return true;
						}
						return false;
				}});
				for(int i = 0; i < files.length; i++){
					try {
						DwgMemoryDriver driver = new DwgMemoryDriver();
						driver.open(files[i]);
						driver.initialize();
						System.out.println("NumElements="+driver.getShapeCount());
						System.out.println("WithRoi="+(driver.getRois().size() != 0));
						driver.close();
					} catch (Exception e) {
						System.out.println("Error durante la lectura del fichero "+i+","+files[i].getName());
						e.printStackTrace();
						break;
					}
				}//i
			}//j
		}
	}

	//test para arreglar el problema de tipos mezclados double[] y Point2D
	public void testLwPline() throws ReadDriverException{
		DwgMemoryDriver driver = new DwgMemoryDriver();
		String fileName = baseDataPath.
			getAbsolutePath() + "/2000/ORENSE-5.DWG";
		File file = new File(fileName);
		driver.open(file);
		driver.initialize();

		//now, the same file has problems with LwPolyline
		fileName = baseDataPath.
			getAbsolutePath() + "/14/ORENSE-5.DWG";
		file = new File(fileName);
		driver.open(file);
		driver.initialize();
	}



}

