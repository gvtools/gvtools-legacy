package com.hardcode.gdbms;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.hardcode.gdbms.engine.data.Tests;
import com.hardcode.gdbms.engine.data.db.DataBaseTests;
import com.hardcode.gdbms.engine.data.edition.EditionTests;
import com.hardcode.gdbms.engine.data.edition.PKDataStructureTest;
import com.hardcode.gdbms.engine.data.file.FilePKAccess;
import com.hardcode.gdbms.engine.data.spatial.PostGISTest;
import com.hardcode.gdbms.engine.data.spatial.SHPTest;
import com.hardcode.gdbms.engine.data.spatial.ViewportTests;
import com.hardcode.gdbms.engine.strategies.AutomaticDataSourceTest;
import com.hardcode.gdbms.engine.strategies.OperationTest;
import com.hardcode.gdbms.engine.strategies.SQLTest;
import com.hardcode.gdbms.engine.values.ComplexValueTest;
import com.hardcode.gdbms.engine.values.ValuesTest;

/**
 * @author Fernando González Cortés
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.hardcode.gdbms.engine.test");
		// $JUnit-BEGIN$
		suite.addTestSuite(Tests.class);
		suite.addTestSuite(EditionTests.class);
		suite.addTestSuite(ValuesTest.class);
		suite.addTestSuite(DataBaseTests.class);
		suite.addTestSuite(FilePKAccess.class);
		suite.addTestSuite(PKDataStructureTest.class);
		suite.addTestSuite(OperationTest.class);
		suite.addTestSuite(SQLTest.class);
		suite.addTestSuite(AutomaticDataSourceTest.class);

		/**
		 * (cesar) added the rest of the TestCases available in gdbms. If some
		 * of them is obsolete, please remove the test from the project, and
		 * remove its entry here.
		 */
		suite.addTestSuite(ComplexValueTest.class);
		suite.addTestSuite(DataSourceTestCase.class);
		suite.addTestSuite(PostGISTest.class);
		suite.addTestSuite(SHPTest.class);
		suite.addTestSuite(ViewportTests.class);
		// $JUnit-END$
		return suite;
	}
}
