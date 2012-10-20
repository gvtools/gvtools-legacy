package com.iver.cit.gvsig.fmap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.iver.cit.gvsig.fmap.layers.FLyrVectTest;

public class GTAllTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.iver.cit.gvsig.fmap");
		suite.addTestSuite(SourceManagerTest.class);
		suite.addTestSuite(FLyrVectTest.class);
		return suite;
	}
}
