package org.gvsig;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.gvsig.layer.FilterTest;
import org.gvsig.layer.LayerTest;
import org.gvsig.layer.SourceManagerTest;
import org.gvsig.layer.SourceTest;
import org.gvsig.map.MapContextTest;

public class AllTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());

		suite.addTestSuite(LayerTest.class);
		suite.addTestSuite(FilterTest.class);
		suite.addTestSuite(SourceManagerTest.class);
		suite.addTestSuite(SourceTest.class);
		suite.addTestSuite(MapContextTest.class);

		return suite;
	}

}
