package com.hardcode.gdbms.engine.strategies;

import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * @author Fernando González Cortés
 */
public class SQLTest extends DataSourceTestCase {
	public void testIsClause() throws Exception {
		DataSource d = ds.executeSQL(
				"select * from persona where apellido is null;",
				DataSourceFactory.MANUAL_OPENING);
		d.start();
		Value v0 = ValueFactory.createValue(2);
		Value v1 = ValueFactory.createValue("fernan");
		Value v2 = ValueFactory.createNullValue();
		assertTrue(d.getRowCount() == 1);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(v0)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(v1)).getValue());
		assertTrue(d.getFieldValue(0, 2) instanceof NullValue);
		assertTrue(((BooleanValue) d.getFieldValue(0,
				d.getFieldIndexByName("PK")).equals(v0)).getValue());
		d.stop();

		d = ds.executeSQL("select * from persona where apellido is not null;",
				DataSourceFactory.MANUAL_OPENING);
		d.start();
		v0 = ValueFactory.createValue(0);
		v1 = ValueFactory.createValue("fernando");
		v2 = ValueFactory.createValue("gonzalez");
		Value v3 = ValueFactory.createValue(1);
		Value v4 = ValueFactory.createValue("huracán");
		Value v5 = ValueFactory.createValue("gonsales");
		assertTrue(d.getRowCount() == 2);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(v0)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(v1)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(v2)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0,
				d.getFieldIndexByName("PK")).equals(v0)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(v3)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(v4)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 2).equals(v5)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1,
				d.getFieldIndexByName("PK")).equals(v3)).getValue());
		d.stop();
	}

	public void testBetweenClause() throws Exception {
		DataSource d = ds.executeSQL(
				"select * from persona where id between 0 and 2;",
				DataSourceFactory.MANUAL_OPENING);
		d.start();
		Value v0 = ValueFactory.createValue(1);
		Value v1 = ValueFactory.createValue("huracán");
		Value v2 = ValueFactory.createValue("gonsales");
		assertTrue(d.getRowCount() == 1);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(v0)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(v1)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(v2)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0,
				d.getFieldIndexByName("PK")).equals(v0)).getValue());
		d.stop();

		d = ds.executeSQL(
				"select * from persona where id not between 0 and 2;",
				DataSourceFactory.MANUAL_OPENING);
		d.start();
		v0 = ValueFactory.createValue(0);
		v1 = ValueFactory.createValue("fernando");
		v2 = ValueFactory.createValue("gonzalez");
		Value v3 = ValueFactory.createValue(2);
		Value v4 = ValueFactory.createValue("fernan");
		Value v5 = ValueFactory.createNullValue();
		assertTrue(d.getRowCount() == 2);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(v0)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(v1)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(v2)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0,
				d.getFieldIndexByName("PK")).equals(v0)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(v3)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(v4)).getValue());
		assertTrue(d.getFieldValue(1, 2) instanceof NullValue);
		assertTrue(((BooleanValue) d.getFieldValue(1,
				d.getFieldIndexByName("PK")).equals(v3)).getValue());
		d.stop();
	}

	public void testInClause() throws Exception {
		DataSource d = ds.executeSQL(
				"select * from persona where id in (0, 2);",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		Value v0 = ValueFactory.createValue(0);
		Value v1 = ValueFactory.createValue("fernando");
		Value v2 = ValueFactory.createValue("gonzalez");
		Value v3 = ValueFactory.createValue(2);
		Value v4 = ValueFactory.createValue("fernan");
		Value v5 = ValueFactory.createNullValue();
		assertTrue(d.getRowCount() == 2);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(v0)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(v1)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(v2)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0,
				d.getFieldIndexByName("PK")).equals(v0)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(v3)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(v4)).getValue());
		assertTrue(d.getFieldValue(1, 2) instanceof NullValue);
		assertTrue(((BooleanValue) d.getFieldValue(1,
				d.getFieldIndexByName("PK")).equals(v3)).getValue());
		d.stop();

		d = ds.executeSQL("select * from persona where id not in (0, 2);",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		v0 = ValueFactory.createValue(1);
		v1 = ValueFactory.createValue("huracán");
		v2 = ValueFactory.createValue("gonsales");
		assertTrue(d.getRowCount() == 1);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(v0)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(v1)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(v2)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0,
				d.getFieldIndexByName("PK")).equals(v0)).getValue());
		d.stop();
	}

	public void testAggregate() throws Exception {
		DataSource d = ds.executeSQL(
				"select count(id) from persona where id < 2;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 1);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(2))).getValue());
		d.stop();

		d = ds.executeSQL("select count(id) from persona;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 1);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(3))).getValue());
		d.stop();
	}

	public void testTwoTimesTheSameAggregate() throws Exception {
		DataSource d = ds
				.executeSQL(
						"select count(id), count(id), sum(id) from persona where id > 0;",
						DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 1);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(
				ValueFactory.createValue(3))).getValue());
		d.stop();

		d = ds.executeSQL("select count(id), count(id), sum(id) from persona;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 1);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(3))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue(3))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(
				ValueFactory.createValue(3))).getValue());
		d.stop();
	}

	public void testOrderByAsc() throws Exception {
		DataSource d = ds.executeSQL(
				"select * from persona where id > 0 order by id asc;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 2);
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(
				ValueFactory.createValue("fernan"))).getValue());
		assertTrue(d.getFieldValue(1, 2) instanceof NullValue);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue("huracán"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(
				ValueFactory.createValue("gonsales"))).getValue());
		d.stop();

		d = ds.executeSQL("select * from persona order by id asc;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 3);
		assertTrue(((BooleanValue) d.getFieldValue(2, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 1).equals(
				ValueFactory.createValue("fernan"))).getValue());
		assertTrue(d.getFieldValue(2, 2) instanceof NullValue);
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(
				ValueFactory.createValue("huracán"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 2).equals(
				ValueFactory.createValue("gonsales"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue("fernando"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(
				ValueFactory.createValue("gonzalez"))).getValue());
		d.stop();
	}

	public void testOrderByDesc() throws Exception {
		DataSource d = ds.executeSQL(
				"select * from persona where id > 0 order by id desc;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 2);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue("fernan"))).getValue());
		assertTrue(d.getFieldValue(0, 2) instanceof NullValue);
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(
				ValueFactory.createValue("huracán"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 2).equals(
				ValueFactory.createValue("gonsales"))).getValue());
		d.stop();

		d = ds.executeSQL("select * from persona order by id desc;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 3);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue("fernan"))).getValue());
		assertTrue(d.getFieldValue(0, 2) instanceof NullValue);
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(
				ValueFactory.createValue("huracán"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 2).equals(
				ValueFactory.createValue("gonsales"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 1).equals(
				ValueFactory.createValue("fernando"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 2).equals(
				ValueFactory.createValue("gonzalez"))).getValue());
		d.stop();
	}

	public void testOrderByWithNullValues() throws Exception {
		DataSource d = ds.executeSQL(
				"select * from persona order by apellido;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 3);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue("fernan"))).getValue());
		assertTrue(d.getFieldValue(0, 2) instanceof NullValue);
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(
				ValueFactory.createValue("huracán"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 2).equals(
				ValueFactory.createValue("gonsales"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 1).equals(
				ValueFactory.createValue("fernando"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 2).equals(
				ValueFactory.createValue("gonzalez"))).getValue());
		d.stop();

		d = ds.executeSQL("select * from persona order by apellido desc;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 3);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue("fernando"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 2).equals(
				ValueFactory.createValue("gonzalez"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(
				ValueFactory.createValue("huracán"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 2).equals(
				ValueFactory.createValue("gonsales"))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 1).equals(
				ValueFactory.createValue("fernan"))).getValue());
		assertTrue(d.getFieldValue(2, 2) instanceof NullValue);
		d.stop();
	}

	public void testOrderWithRepeatedValues() throws Exception {
		DataSource d = ds.executeSQL("select id2 from sort order by id2 asc;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 5);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 0).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 0).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(4, 0).equals(
				ValueFactory.createValue(3))).getValue());
		d.stop();
	}

	public void testOrderByMultipleCriteria() throws Exception {
		DataSource d = ds.executeSQL(
				"select * from sort order by id1 desc, id2 asc;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 5);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 1).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 1).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(4, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(4, 1).equals(
				ValueFactory.createValue(3))).getValue());
		d.stop();
	}

	public void testDistinct() throws Exception {
		DataSource d = ds.executeSQL("select distinct id1 from sort ;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 2);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(2))).getValue());
		d.stop();
	}

	public void testDistinctManyFields() throws Exception {
		DataSource d = ds.executeSQL("select distinct id1, id2 from sort ;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 4);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(
				ValueFactory.createValue(3))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 1).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 1).equals(
				ValueFactory.createValue(1))).getValue());
		d.stop();
	}

	public void testDistinctAllFields() throws Exception {
		DataSource d = ds.executeSQL("select distinct * from sort ;",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 5);
		assertTrue(((BooleanValue) d.getFieldValue(0, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(1, 1).equals(
				ValueFactory.createValue(3))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(2, 1).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 0).equals(
				ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 1).equals(
				ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(4, 0).equals(
				ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(4, 1).equals(
				ValueFactory.createValue(0))).getValue());
		d.stop();
	}
}
