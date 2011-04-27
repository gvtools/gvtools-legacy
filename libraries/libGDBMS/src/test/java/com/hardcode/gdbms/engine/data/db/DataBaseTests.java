package com.hardcode.gdbms.engine.data.db;

import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class DataBaseTests extends DataSourceTestCase {
	/**
	 * Access to the PK field
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testPKAccess() throws Exception {
		DataSource d = ds.createRandomDataSource("hsqldbpersona",
				DataSourceFactory.MANUAL_OPENING);
		d.start();

		int[] pks = d.getPrimaryKeys();
		assertTrue(pks.length == 1);
		assertTrue(pks[0] == 6);
		d.stop();
	}
}
