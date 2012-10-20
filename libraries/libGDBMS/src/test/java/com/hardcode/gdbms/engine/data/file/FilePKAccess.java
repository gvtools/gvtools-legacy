package com.hardcode.gdbms.engine.data.file;

import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando González Cortés
 */
public class FilePKAccess extends DataSourceTestCase {
	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testname() throws Exception {
		DataSource d = ds.createRandomDataSource("persona",
				DataSourceFactory.MANUAL_OPENING);
		d.start();

		int[] pks = d.getPrimaryKeys();
		assertTrue(pks.length == 1);
		assertTrue(pks[0] == (d.getFieldCount() - 1));
		d.stop();
	}
}
