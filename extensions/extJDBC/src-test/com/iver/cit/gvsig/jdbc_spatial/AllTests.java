package com.iver.cit.gvsig.jdbc_spatial;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.iver.cit.gvsig.jdbc_spatial");
		//$JUnit-BEGIN$
		suite.addTestSuite(testPostGIS.class);
		//$JUnit-END$
		return suite;
	}

}
