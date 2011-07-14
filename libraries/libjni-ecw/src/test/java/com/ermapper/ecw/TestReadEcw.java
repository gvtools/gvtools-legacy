package com.ermapper.ecw;

import junit.framework.TestCase;

public class TestReadEcw extends TestCase{

	private JNCSFile 				file = null;
	
	private String 					fileName = "miniraster30x30.jp2";
	private String 					baseDir = "./test-images/";
	private String 					file1 = baseDir + fileName;
	
	private int						numBands = 0;
	private int 					width = 0;
	private int						height = 0;
	private String					projection = null;
	
	
	public void start(){
		setUp();
		testStack();
	}
	
	public void setUp(){
		try {
			System.out.println("***** TEST DE ACCESO A ECW *****");
			System.out.println("     IMAGEN: " + fileName + "\n");
			file = new JNCSFile();
			file.open(file1, true);
			System.out.println("Acedo a algunos datos de la imagen: ");
			numBands = file.numBands;
			height = file.height;
			width = file.width;
			projection = file.projection;
			System.out.println("Número de bandas: " + numBands);
			System.out.println("Alto: " + height);
			System.out.println("Ancho: " + width);
			System.out.println("Proyección: " + projection);
			
		} catch (JNCSException e) {
			e.printStackTrace();
		}
	}
	
	public void testStack(){
		
	}
	
}
