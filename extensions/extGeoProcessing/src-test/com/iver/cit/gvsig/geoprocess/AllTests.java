package com.iver.cit.gvsig.geoprocess;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.iver.cit.gvsig.geoprocess.impl.buffer.BufferTest;
import com.iver.cit.gvsig.geoprocess.impl.difference.DifferenceTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.iver.cit.gvsig.geoprocess");
		// $JUnit-BEGIN$
		suite.addTestSuite(BufferTest.class);
		suite.addTestSuite(DifferenceTest.class);
		// $JUnit-END$
		return suite;
	}

}
