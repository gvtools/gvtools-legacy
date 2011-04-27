package com.iver.cit.jdwglib.dwg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import com.iver.cit.jdwglib.dwg.objects.DwgBlockHeader;
import com.iver.cit.jdwglib.dwg.objects.DwgPFacePolyline;

public class DwgFileTest extends TestCase {
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

	public void test1() throws IOException, DwgVersionNotSupportedException {
		// String fileName = baseDataPath.getAbsolutePath()+"/Un punto.dwg";
		// DwgFile dwg = new DwgFile(fileName);
		//
		// dwg.read();
		// dwg.calculateGisModelDwgPolylines();
		// dwg.blockManagement();
		// LinkedList dwgObjects = dwg.getDwgObjects();
	}

	

	// test of DWG 12 format
	public void test4() throws IOException, DwgVersionNotSupportedException {
//		String fileName = baseDataPath.getAbsolutePath() + "/TORRE03.DWG";
//		DwgFile dwg = new DwgFile(fileName);
//		dwg.read();
//		dwg.calculateGisModelDwgPolylines();
//		//antes de los bloques
//		List dwgObjects = dwg.getDwgObjects();
//		dwg.blockManagement2();
//		//despues de los bloques
//		dwgObjects = dwg.getDwgObjects();
//		for(int i = 0; i < dwgObjects.size(); i++){
//			DwgObject o = (DwgObject) dwgObjects.get(i);
//			if(o instanceof IDwg2FMap){
//				((IDwg2FMap)o).toFMapGeometry(true);
//			}
//		}
	}
	
	public void test5() throws IOException, DwgVersionNotSupportedException{
		 String fileName = baseDataPath.getAbsolutePath()+"/TORRE03.DWG";
		 DwgFile dwg = new DwgFile(fileName);
		
		 dwg.read();
		 List dwgObjects = dwg.getDwgObjects();
		 for(int i = 0; i < dwgObjects.size(); i++){
			 DwgObject obj = (DwgObject) dwgObjects.get(i);
			 if(obj instanceof DwgBlockHeader){
				 DwgBlockHeader blockHeader = (DwgBlockHeader)obj;
				 if(blockHeader.isBlkIsXRef()){
					 System.out.println("bloque "+blockHeader.getName()+" es referencia externa");
					 System.out.println("path="+blockHeader.getXRefPName());
				 }
			 }
		 }
	}

}
