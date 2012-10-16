package com.hardcode.gdbms.engine.data.edition;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class EditionTests extends DataSourceTestCase {
    
	/**
	 * DOCUMENT ME!
	 * @param mode
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	private void testDelete(String dsName, int mode) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);
		
		dw.beginTrans();
		dw.setFieldValue(0, 1, ValueFactory.createValue("joder"));
		dw.insertEmptyRow(ValueFactory.createValue(
				new Value[] { ValueFactory.createValue(4) }));
		dw.deleteRow(0); //0
		dw.deleteRow(0); //1
		dw.deleteRow(1); //3

		dw.commitTrans();
		
		d = ds.createRandomDataSource(dsName, DataSourceFactory.MANUAL_OPENING);
		d.start();
		assertTrue(((BooleanValue)d.getFieldValue(0, 0).equals(ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(0, 1).equals(ValueFactory.createValue("fernan"))).getValue());
		assertTrue(d.getFieldValue(0, 2) instanceof NullValue);
		assertTrue(d.getRowCount() == 1);
		d.stop();
	}
	
	public void testDeleteFakeTransaction() throws Exception {
	    testDelete("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testDelete("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testDelete("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	}

    public void testDeleteCoherentRowTransaction() throws Exception {
        testDelete("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }

    public void testDeleteDirectTransaction() throws Exception {
        testDelete("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_DIRECT_MODE);
    }

	/**
	 * DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	private void testUpdate(String dsName, int mode) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);

		dw.beginTrans();
		dw.insertEmptyRow(ValueFactory.createValue(
				new Value[] { ValueFactory.createValue(3) }));
		dw.setFieldValue(0, 1, ValueFactory.createValue("edicion1"));
		dw.setFieldValue(1, 1, ValueFactory.createValue("edicion2"));
		dw.setFieldValue(1, 2, ValueFactory.createValue("edicion2"));
		dw.setFieldValue(3, 1, ValueFactory.createValue("edicion3"));
		dw.setFieldValue(3, 0, ValueFactory.createValue(3));

		dw.commitTrans();

		d = ds.createRandomDataSource(dsName, DataSourceFactory.MANUAL_OPENING);
		d.start();
		assertTrue(((BooleanValue)d.getFieldValue(0, 0).equals(ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(0, 1).equals(ValueFactory.createValue("edicion1"))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(0, 2).equals(ValueFactory.createValue("gonzalez"))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(1, 0).equals(ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(1, 1).equals(ValueFactory.createValue("edicion2"))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(1, 2).equals(ValueFactory.createValue("edicion2"))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(2, 0).equals(ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(2, 1).equals(ValueFactory.createValue("fernan"))).getValue());
		assertTrue(d.getFieldValue(2, 2) instanceof NullValue);
		assertTrue(((BooleanValue)d.getFieldValue(3, 0).equals(ValueFactory.createValue(3))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(3, 1).equals(ValueFactory.createValue("edicion3"))).getValue());
		assertTrue(d.getFieldValue(3, 2) instanceof NullValue);
		assertTrue(d.getRowCount() == 4);
		d.stop();
	}
	
	public void testUpdateFakeTransaction() throws Exception {
	    testUpdate("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testUpdate("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testUpdate("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	}

    public void testUpdateCoherentRowTransaction() throws Exception {
        testUpdate("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_DIRECT_MODE);
    }

    public void testUpdateDirectTransaction() throws Exception {
        testUpdate("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }
	
	private void testUpdatePK(String dsName, int mode) throws Exception{
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);

		dw.beginTrans();
		dw.setFieldValue(0, 0, ValueFactory.createValue(10));
		dw.commitTrans();

		d = ds.executeSQL("select * from " + dsName + " where ID = 10;", DataSourceFactory.MANUAL_OPENING);
		d.start();
		assertTrue(((BooleanValue)d.getFieldValue(0, 0).equals(ValueFactory.createValue(10))).getValue());
		d.stop();
    }
	
	public void testUpdatePKFakeTransaction() throws Exception {
	    testUpdatePK("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testUpdatePK("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testUpdatePK("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	}

    public void testUpdatePKCoherentRowTransaction() throws Exception {
        testUpdatePK("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }

    public void testUpdatePKDirectTransaction() throws Exception {
        testUpdatePK("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_DIRECT_MODE);
    }
	
	private void testUpdatePKUpdatedRow(String dsName, int mode) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);

		dw.beginTrans();
		dw.setFieldValue(0, 0, ValueFactory.createValue(10));
		dw.setFieldValue(0, 1, ValueFactory.createValue("nombre"));
		dw.commitTrans();

		d = ds.executeSQL("select * from " + dsName + " where ID = 10;", DataSourceFactory.MANUAL_OPENING);
		d.start();
		assertTrue(((BooleanValue)d.getFieldValue(0, 0).equals(ValueFactory.createValue(10))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(0, 1).equals(ValueFactory.createValue("nombre"))).getValue());
		d.stop();
	}
	
	public void testUpdatePKUpdatedRowFakeTransaction() throws Exception {
	    testUpdatePKUpdatedRow("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testUpdatePKUpdatedRow("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testUpdatePKUpdatedRow("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	}

    public void testUpdatePKUpdatedRowCoherentRowTransaction() throws Exception {
        testUpdatePKUpdatedRow("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }

	private void testValuesDuringEdition(String dsName, int mode) throws Exception{
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);
		
		dw.beginTrans();
		dw.setFieldValue(0, 6, ValueFactory.createValue(10));
		assertTrue(((BooleanValue)dw.getFieldValue(0, 6).equals(ValueFactory.createValue(10))).getValue());
		assertTrue(((BooleanValue)dw.getFieldValue(0, 1).equals(ValueFactory.createValue("fernando"))).getValue());
		assertTrue(((BooleanValue)dw.getPKValue(0).getValues()[0].equals(ValueFactory.createValue(10))).getValue());
		dw.setFieldValue(0, 1, ValueFactory.createValue("nombre"));
		assertTrue(((BooleanValue)dw.getFieldValue(0, 1).equals(ValueFactory.createValue("nombre"))).getValue());
		dw.insertEmptyRow(ValueFactory.createValue(new Value[]{ValueFactory.createValue(4)}));
		assertTrue(((BooleanValue)dw.getFieldValue(3, 6).equals(ValueFactory.createValue(4))).getValue());
		dw.rollBackTrans();
   }
	
	public void testValuesDuringEditionFakeTransaction() throws Exception {
	    testValuesDuringEdition("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testValuesDuringEdition("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testValuesDuringEdition("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	}

    public void testValuesDuringEditionCoherentRowTransaction() throws Exception {
        testValuesDuringEdition("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }

    public void testValuesDuringEditionDirectTransaction() throws Exception {
		DataSource d = ds.createRandomDataSource("hsqldbpersonatransactional",
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(DataSourceFactory.DATA_WARE_DIRECT_MODE);
		
		dw.beginTrans();
		dw.setFieldValue(0, 2, ValueFactory.createValue("10"));
		assertTrue(((BooleanValue)dw.getFieldValue(0, 2).equals(ValueFactory.createValue(10))).getValue());
		assertTrue(((BooleanValue)dw.getFieldValue(0, 1).equals(ValueFactory.createValue("fernando"))).getValue());
		dw.setFieldValue(0, 1, ValueFactory.createValue("nombre"));
		assertTrue(((BooleanValue)dw.getFieldValue(0, 1).equals(ValueFactory.createValue("nombre"))).getValue());
		dw.rollBackTrans();
    }
	
	/**
	 * DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	private void testAdd(String dsName, int mode) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);
		
		dw.beginTrans();
		dw.insertEmptyRow(ValueFactory.createValue(
				new Value[] { ValueFactory.createValue(3) }));
		dw.commitTrans();

		d = ds.createRandomDataSource(dsName, DataSourceFactory.MANUAL_OPENING);
		d.start();
		assertTrue(((BooleanValue)d.getFieldValue(0, 0).equals(ValueFactory.createValue(0))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(0, 1).equals(ValueFactory.createValue("fernando"))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(0, 2).equals(ValueFactory.createValue("gonzalez"))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(1, 0).equals(ValueFactory.createValue(1))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(1, 1).equals(ValueFactory.createValue("huracán"))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(1, 2).equals(ValueFactory.createValue("gonsales"))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(2, 0).equals(ValueFactory.createValue(2))).getValue());
		assertTrue(((BooleanValue)d.getFieldValue(2, 1).equals(ValueFactory.createValue("fernan"))).getValue());
		assertTrue(d.getFieldValue(2, 2) instanceof NullValue);
		assertTrue(((BooleanValue)d.getFieldValue(3, d.getFieldIndexByName("PK")).equals(ValueFactory.createValue(3))).getValue());
		assertTrue(d.getFieldValue(3, 1) instanceof NullValue);
		assertTrue(d.getFieldValue(3, 2) instanceof NullValue);
		assertTrue(d.getRowCount() == 4);
		d.stop();
	}
	
	public void testAddFakeTransaction() throws Exception {
	    testAdd("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testAdd("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testAdd("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	}

    public void testAddCoherentRowTransaction() throws Exception {
        testAdd("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }

    public void testAddDirectTransaction() throws Exception {
        testAdd("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_DIRECT_MODE);
    }
	
	private void testSQLInjection(String dsName, int mode) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);

		Value value = ValueFactory.createValue("aaa'aaa");
		
		dw.beginTrans();
		dw.setFieldValue(0, 1, value);
		dw.commitTrans();

		d.start();
		assertTrue(((BooleanValue) d.getFieldValue(0, 1).equals(value)).getValue());
		d.stop();
    }
	
	public void testSQLInjectionFakeTransaction() throws Exception {
	    testSQLInjection("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testSQLInjection("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testSQLInjection("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	}

    public void testSQLInjectionCoherentRowTransaction() throws Exception {
        testSQLInjection("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }

    public void testSQLInjectionDirectTransaction() throws Exception {
        testSQLInjection("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_DIRECT_MODE);
    }
	
	private void testInsertFilledRow(String dsName, int mode) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);

		Value v1 = ValueFactory.createValue(4);
		Value v2 = ValueFactory.createValue("nombre");
		Value v3 = ValueFactory.createValue("apellido");
		Value v4 = ValueFactory.createValue(Date.valueOf("1998-09-05"));
		Value v5 = ValueFactory.createValue(Time.valueOf("4:30:01"));
		Value v6 = ValueFactory.createValue(Timestamp.valueOf("2005-09-05 4:30:01.666666666"));
		Value v7 = ValueFactory.createValue(3);

		dw.beginTrans();
		dw.insertFilledRow(new Value[] {v1, v2, v3, v4, v5, v6, v7});
		dw.commitTrans();

        d.start();
		assertTrue(((BooleanValue) d.getFieldValue(3, 0).equals(v1)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 1).equals(v2)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 2).equals(v3)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 3).equals(v4)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 4).equals(v5)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 5).equals(v6)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 6).equals(v7)).getValue());
		d.stop();
    }
	
	public void testInsertFilledRowFakeTransaction() throws Exception {
	    testInsertFilledRow("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testInsertFilledRow("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testInsertFilledRow("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	}

    public void testInsertFilledRowCoherentRowTransaction() throws Exception {
        testInsertFilledRow("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }

    public void testInsertFilledDirectTransaction() throws Exception {
        testInsertFilledRow("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_DIRECT_MODE);
    }
    
    private void testEditingNullValues(String dsName, int mode) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);

		Value v1 = ValueFactory.createValue(4);
		Value v2 = ValueFactory.createNullValue();
		Value v3 = ValueFactory.createValue("apellido");
		Value v4 = ValueFactory.createValue(Date.valueOf("1998-09-05"));
		Value v5 = ValueFactory.createValue(Time.valueOf("4:30:01"));
		Value v6 = ValueFactory.createValue(Timestamp.valueOf("2005-09-05 4:30:01.666666666"));
		Value v7 = ValueFactory.createValue(3);

		dw.beginTrans();
		dw.insertFilledRow(new Value[] {v1, v2, v3, v4, v5, v6, v7});
		dw.setFieldValue(0, 1, ValueFactory.createNullValue());
		dw.commitTrans();

		d.start();
		assertTrue(((BooleanValue) d.getFieldValue(3, 0).equals(v1)).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getFieldValue(3, 1) instanceof NullValue)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 2).equals(v3)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 3).equals(v4)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 4).equals(v5)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 5).equals(v6)).getValue());
		assertTrue(((BooleanValue) d.getFieldValue(3, 6).equals(v7)).getValue());
		d.stop();
    }
	
	public void testEditingNullValuesFakeTransaction() throws Exception {
	    testEditingNullValues("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testEditingNullValues("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testEditingNullValues("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	}

    public void testEditingNullValuesCoherentRowTransaction() throws Exception {
        testEditingNullValues("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }

    public void testEditingNullValuesDirectTransaction() throws Exception {
        testEditingNullValues("hsqldbpersonatransactional", DataSourceFactory.DATA_WARE_DIRECT_MODE);
    }
    
    private void testEditingDates(String dsName, int mode) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(mode);

		Value v1 = ValueFactory.createValue(4);
		Value v2 = ValueFactory.createNullValue();
		Value v3 = ValueFactory.createValue("apellido");
		Value v4 = ValueFactory.createValue(Date.valueOf("1998-09-05"));
		Value v5 = ValueFactory.createValue(Time.valueOf("4:30:01"));
		Value v6 = ValueFactory.createValue(Timestamp.valueOf("2005-09-05 4:30:01.666666666"));
		Value v7 = ValueFactory.createValue(3);
		
		dw.beginTrans();
		dw.insertFilledRow(new Value[] {v1, v2, v3, v4, v5, v6, v7});
		dw.setFieldValue(0, 1, ValueFactory.createNullValue());
		dw.commitTrans();
    }
    
    public void testEditingDatesFakeTransaction() throws Exception {
	    testEditingDates("hsqldbpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testEditingDates("persona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
	    testEditingDates("objectpersona", DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
    }
    
    public void testAutomaticDataSource() throws Exception{
		DataSource d = ds.createRandomDataSource("persona",
				DataSourceFactory.MANUAL_OPENING);

		DataWare dw = d.getDataWare(DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
        dw.beginTrans();
        dw.setFieldValue(0, 1, ValueFactory.createValue("la gacela"));
        dw.commitTrans();
        
		d = ds.createRandomDataSource("hsqldbpersona",
				DataSourceFactory.MANUAL_OPENING);

		dw = d.getDataWare(DataSourceFactory.DATA_WARE_COHERENT_ROW_ORDER);
        dw.beginTrans();
        dw.setFieldValue(0, 1, ValueFactory.createValue("la gacela"));
        dw.commitTrans();
        
		assertTrue(true);
    }
}
