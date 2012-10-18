package com.iver.cit.gvsig.fmap;

import com.iver.cit.gvsig.fmap.core.symbols.TestISymbol;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GTAllTests extends TestCase {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.iver.cit.gvsig.fmap");
		suite.addTestSuite(SourceManagerTest.class);
		
		return suite;
	}
}
