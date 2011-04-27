package org.gvsig.remoteClient.sld.symbolizerTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSLDSymbolizers extends TestCase {


	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Tests for SLDSymbolizers");
		
		suite.addTestSuite(TestParseLineSymbolizer.class);
		suite.addTestSuite(TestParsePointSymbolizer.class);
		suite.addTestSuite(TestParsePolygonSymbolizer.class);
		
		return suite;
	}


}
