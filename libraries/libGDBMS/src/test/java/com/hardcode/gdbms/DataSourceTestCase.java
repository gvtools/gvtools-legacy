package com.hardcode.gdbms;

import junit.framework.TestCase;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.SetUp;

/**
 * 
 */
public class DataSourceTestCase extends TestCase {

	protected DataSourceFactory ds;

	protected void setUp() throws Exception {
		ds = SetUp.setUp();
	}

}
