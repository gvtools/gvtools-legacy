package com.hardcode.gdbms;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.SetUp;

import junit.framework.TestCase;

/**
 * 
 */
public class DataSourceTestCase extends TestCase{

	protected DataSourceFactory ds;
	protected void setUp() throws Exception {
	    ds = SetUp.setUp();
	}


}
