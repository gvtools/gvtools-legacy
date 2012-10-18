package org.gvsig.geotools.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class GTAllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite(GTAllTests.class.getName());
		suite.addTestSuite(GTFLyrVectTest.class);
		return suite;
	}
}
