/* gvSIG. Geographic Information System of the Valencian Government
*
* Copyright (C) 2007-2008 Infrastructures and Transports Department
* of the Valencian Government (CIT)
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
* 2008 IVER   {{Task}}
*/
package com.iver.cit.gvsig.gui.styling;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.tree.DefaultMutableTreeNode;

import junit.framework.TestCase;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.xml.XMLEncodingUtils;
import com.iver.utiles.xmlEntity.generate.XmlTag;

/**
 * Test to check the correct operation of the symbol library. Adds a created symbol
 * and recovers it using a name. At the end the folder and the symbol will be eliminated
 *
 * @author Eustaquio Vercher Gómez
 */
public class TestSymbolLibrary extends TestCase {
	private String folderTestName = "_____TestFolder_____/";
	private String test_fileName = "____testsym____"+System.currentTimeMillis()+ SymbolLibrary.SYMBOL_FILE_EXTENSION;
	private SymbolLibrary lib = SymbolLibrary.getInstance();
	private File testFolder =  new File(SymbologyFactory.SymbolLibraryPath + File.separator + folderTestName);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// this only prepares the test, it is not the test itself
		File f = new File(SymbologyFactory.SymbolLibraryPath + File.separator + folderTestName);
		if (f.exists()) {
			deleteRecursively(f);
		}

	};

	@Override
	public void tearDown(){
		// this only prepares the test, it is not the test itself
		File f = new File(SymbologyFactory.SymbolLibraryPath + File.separator + folderTestName);
		if (f.exists()) {
			deleteRecursively(f);
		}
	}
	
	private void deleteRecursively(File f) {
		if (f.isDirectory()) {
			for (int i = f.list().length-1; i >= 0; i--) {
				deleteRecursively(new File(f.getAbsolutePath()+File.separator+f.list()[i]));
			}
		}
		f.delete();
	}

	/**
	 * Tests the correct operation of the library
	 */
	public void testAddFolder(){
		
		int childs = lib.getChildCount(lib.getRoot());

		//adding one folder
		lib.addFolder(lib.getRoot(), folderTestName);
		int childsExpected = lib.getChildCount(lib.getRoot());

		assertEquals("Folder does not have been added to Library",childs+1,childsExpected);
	}
	
	

	public void testAddSymbol() {
		testAddFolder();
		
		//add symbol to the created folder
		ILineSymbol lineIn = getTestLineSymbol();
		lib.addElement(lineIn, test_fileName, testFolder);

		DefaultMutableTreeNode lineAux = (DefaultMutableTreeNode) lib.getElement(
				testFolder, test_fileName);
		
		File obj = (File)lineAux.getUserObject();

		assertTrue("File does not exist", obj.exists());
	}
	
	private ILineSymbol getTestLineSymbol() {
		ILineSymbol lineIn = (ILineSymbol) SymbologyFactory.createDefaultLineSymbol();
		lineIn.setLineWidth(328975); // a non usual value for the test (avoid false positives)
		lineIn.setDescription("TestingSymbolLibrary");
		return lineIn;
	}
	

	public void testGetSymbol() {
		testAddSymbol();
		DefaultMutableTreeNode lineAux = (DefaultMutableTreeNode) lib.getElement(testFolder, test_fileName);
		File obj = (File)lineAux.getUserObject();

		ILineSymbol lineIn = getTestLineSymbol();
		XMLEntity xml = null;
		try {
			xml = new XMLEntity((XmlTag) XmlTag.unmarshal(XMLEncodingUtils.getReader(obj)));
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (ValidationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ILineSymbol lineOut = (ILineSymbol) SymbologyFactory.createSymbolFromXML(xml, obj.getName());

		assertTrue("Symbol in library is different to created symbol",
				lineIn.getLineWidth() == lineOut.getLineWidth() &&
				lineIn.getDescription().equals(lineIn.getDescription()) &&
				lineIn.getClassName().equals(lineOut.getClassName()));
	}
	
	public void testRemoveElement() {
		testAddSymbol(); // <- to previously create the test symbol
		
		assertTrue("removing something that does not existr", 
				lib.getElement(testFolder, test_fileName) != null);
		//delete created element
		lib.removeElement(test_fileName, testFolder);
		assertFalse("library didn't delete the symbol", 
				lib.getElement(testFolder, test_fileName) != null);
		
		// TODO faltaría ver si lo que se ha eliminado es lo que esperamos!
	}

	public void testRemoveFolder() {
		testAddSymbol(); // <- to previously create the test symbol
		/*
		 * For the childs, childsExpected, lineAux and obj variables to contain
		 * right values, it is necessary that the tests testAddFolder, testAddSymbol and testGetSymbol
		 * do not fail before to run this one. If one of the previous test has failed,
		 * the result of this test will not be correct.
		 */
		int childsBefore = lib.getChildCount(lib.getRoot());
		
		//delete folder
		File f = new File(SymbologyFactory.SymbolLibraryPath);
		lib.removeElement(folderTestName,f);
		int childsAfter = lib.getChildCount(lib.getRoot());

		assertEquals("Folder is not deleted", childsBefore, childsAfter+1);

	}
	

}
