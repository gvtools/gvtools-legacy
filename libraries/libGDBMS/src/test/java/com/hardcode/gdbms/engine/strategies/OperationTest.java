package com.hardcode.gdbms.engine.strategies;

import java.io.FileInputStream;
import java.io.IOException;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.engine.customQuery.QueryManager;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.hardcode.gdbms.parser.ParseException;

/**
 *
 */
public class OperationTest extends DataSourceTestCase {
	static {
		QueryManager.registerQuery(new SumQuery());
	}

	public void testOrderOperationDataSource() throws Exception {
		DataSource d = ds
				.executeSQL(
						"(select id, nombre, apellido from persona) union (select  id, nombre, apellido from persona);",
						DataSourceFactory.MANUAL_OPENING);

		String sql = "select * from '" + d.getName() + "' order by id;";

		d.getDataSourceFactory().executeSQL(sql,
				DataSourceFactory.MANUAL_OPENING);

	}

	/**
	 * test a union query
	 * 
	 * @throws Throwable
	 *             DOCUMENT ME!
	 */
	public void testUnion() throws Throwable {
		DataSource d = ds
				.executeSQL(
						"(select id, nombre, apellido from persona) union (select  id, nombre, apellido from persona);",
						DataSourceFactory.MANUAL_OPENING);
		d.start();
		String aux = d.getAsString();
		d.stop();

		FileInputStream fis = new FileInputStream(
				"src/test/resources/union.txt");
		byte[] correcto = new byte[aux.getBytes().length];
		fis.read(correcto);
		fis.close();

		assertTrue("La unión no es correcta",
				aux.toString().equals(new String(correcto)));
	}

	/**
	 * Tests a simple select query
	 * 
	 * @throws Throwable
	 *             DOCUMENT ME!
	 */
	public void testSelect() throws Throwable {
		DataSource d = ds.executeSQL(
				"select apellido, PK from persona where nombre='fernando';",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		String aux = d.getAsString();
		d.stop();

		FileInputStream fis = new FileInputStream(
				"src/test/resources/select.txt");
		byte[] correcto = new byte[aux.getBytes().length];
		fis.read(correcto);
		fis.close();

		assertTrue("La select no es correcta", aux.equals(new String(correcto)));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws DriverLoadException
	 *             DOCUMENT ME!
	 * @throws ParseException
	 *             DOCUMENT ME!
	 * @throws DriverException
	 *             DOCUMENT ME!
	 * @throws SemanticException
	 *             DOCUMENT ME!
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void testCustomQuery() throws Exception, SemanticException,
			IOException {
		DataSource d = ds.executeSQL(
				"custom sumquery tables persona values (id);",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(3))).getValue());
		d.stop();
	}

}
