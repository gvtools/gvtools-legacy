package com.iver.cit.gvsig.geoprocess;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.iver.cit.gvsig.geoprocess");
		// $JUnit-BEGIN$
		suite.addTestSuite(LineCleanTest.class);
		// $JUnit-END$
		return suite;
	}

}
