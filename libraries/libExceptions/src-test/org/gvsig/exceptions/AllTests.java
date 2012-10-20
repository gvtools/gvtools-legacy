package org.gvsig.exceptions;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.gvsig.exceptions");
		// $JUnit-BEGIN$
		suite.addTestSuite(ListBaseExceptionTest.class);
		suite.addTestSuite(BaseExceptionTest.class);
		// $JUnit-END$
		return suite;
	}

}
