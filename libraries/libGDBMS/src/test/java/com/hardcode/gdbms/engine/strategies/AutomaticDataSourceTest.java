package com.hardcode.gdbms.engine.strategies;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;

/**
 * @author Fernando González Cortés
 */
public class AutomaticDataSourceTest extends DataSourceTestCase {
	public void testGetFieldNames() {
		DataSource d;
		try {
			d = ds.createRandomDataSource("persona",
					DataSourceFactory.AUTOMATIC_OPENING);
			d.getFieldNames();
			assertTrue(true);
			return;
		} catch (DriverLoadException e) {
		} catch (NoSuchTableException e) {
		} catch (ReadDriverException e) {
		}
		assertTrue(false);
	}
}
