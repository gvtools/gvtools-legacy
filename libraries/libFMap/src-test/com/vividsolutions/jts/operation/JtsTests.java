package com.vividsolutions.jts.operation;


import junit.framework.Test;
import junit.framework.TestSuite;

import com.vividsolutions.jts.operation.overlay.SnappingOverlayOperationTest;

public class JtsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.iver.cit.gvsig.fmap.topology");
		//$JUnit-BEGIN$
		suite.addTestSuite(SnappingOverlayOperationTest.class);
		//$JUnit-END$
		return suite;
	}

}

