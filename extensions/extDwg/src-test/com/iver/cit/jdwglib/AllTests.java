package com.iver.cit.jdwglib;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.iver.cit.jdwglib");
		//$JUnit-BEGIN$
		suite.addTestSuite(DwgRegionOfInterestTestCase.class);
		suite.addTestSuite(DwgFilesTestCase.class);
		//$JUnit-END$
		return suite;
	}

}
