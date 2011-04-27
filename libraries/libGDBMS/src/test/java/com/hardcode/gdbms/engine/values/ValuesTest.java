package com.hardcode.gdbms.engine.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class ValuesTest extends DataSourceTestCase {
    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testArrayValue() throws Exception {
        Value[] v = new Value[7];

        for (int i = 0; i < v.length; i++) {
            v[i] = ValueFactory.createValue(i);
        }

        ValueCollection av = ValueFactory.createValue(v);

        ValueCollection av1 = ValueFactory.createValue(new Value[] {
                    ValueFactory.createValue(0)
                });

        for (int i = 0; i < v.length; i++) {
            v[i] = ValueFactory.createValue(i);
        }

        ValueCollection av2 = ValueFactory.createValue(v);

        assertTrue(((BooleanValue) av.equals(av2)).getValue());
        assertTrue(av.hashCode() == av2.hashCode());

        /*
         * This assertion may fail when the code is right. Pass the
         * test again
         */
        assertTrue(av.hashCode() != av1.hashCode());

        for (int i = 0; i < 7; i++) {
            assertTrue(((BooleanValue) av.get(i).equals(ValueFactory.createValue(
                        i))).getValue());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param dsName DOCUMENT ME!
     *
     * @throws NoSuchTableException DOCUMENT ME!
     * @throws DriverException DOCUMENT ME!
     * @throws DriverLoadException
     */
    private void doTestNullValues(String dsName)
        throws NoSuchTableException, ReadDriverException, DriverLoadException {
        DataSource d = ds.createRandomDataSource(dsName,
                DataSourceFactory.MANUAL_OPENING);
        d.start();

        for (int i = 0; i < d.getRowCount(); i++) {
            for (int j = 0; j < d.getFieldCount(); j++) {
                assertTrue(d.getFieldValue(i, j) != null);
                assertFalse(d.getFieldValue(i, j).toString().equals("'null'"));
            }
        }

        d.stop();
    }

    /**
     * Tests the DataSources never return null instead of NullValue
     *
     * @throws Throwable DOCUMENT ME!
     */
    public void testNullValues() throws Throwable {
        doTestNullValues("nulos");
        doTestNullValues("persona");
        doTestNullValues("objectpersona");
    }

    /**
     * Tests the NullValues operations
     */
    public void testNullValueOperations() {
        Value n = ValueFactory.createNullValue();

        try {
            ValueCollection b = ValueFactory.createValue(new Value[0]);
            assertFalse(((BooleanValue)b.equals(n)).getValue());
            assertFalse(((BooleanValue)b.notEquals(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value b = ValueFactory.createValue(true);
            b.and(n);
            b.or(n);
            assertFalse(((BooleanValue)b.equals(n)).getValue());
            assertFalse(((BooleanValue)b.notEquals(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value i = ValueFactory.createValue(1);
            i.equals(n);
            i.notEquals(n);
            assertFalse(((BooleanValue)i.less(n)).getValue());
            assertFalse(((BooleanValue)i.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)i.greater(n)).getValue());
            assertFalse(((BooleanValue)i.greaterEqual(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value s = ValueFactory.createValue("test");
            assertFalse(((BooleanValue)s.equals(n)).getValue());
            assertFalse(((BooleanValue)s.notEquals(n)).getValue());
            assertFalse(((BooleanValue)s.less(n)).getValue());
            assertFalse(((BooleanValue)s.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)s.greater(n)).getValue());
            assertFalse(((BooleanValue)s.greaterEqual(n)).getValue());
            s.like(n);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value d = ValueFactory.createValue(new Date());
            assertFalse(((BooleanValue)d.equals(n)).getValue());
            assertFalse(((BooleanValue)d.notEquals(n)).getValue());
            assertFalse(((BooleanValue)d.less(n)).getValue());
            assertFalse(((BooleanValue)d.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)d.greater(n)).getValue());
            assertFalse(((BooleanValue)d.greaterEqual(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value t = ValueFactory.createValue(new Time(12));
            assertFalse(((BooleanValue)t.equals(n)).getValue());
            assertFalse(((BooleanValue)t.notEquals(n)).getValue());
            assertFalse(((BooleanValue)t.less(n)).getValue());
            assertFalse(((BooleanValue)t.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)t.greater(n)).getValue());
            assertFalse(((BooleanValue)t.greaterEqual(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            Value ts = ValueFactory.createValue(new Timestamp(12));
            assertFalse(((BooleanValue)ts.equals(n)).getValue());
            assertFalse(((BooleanValue)ts.notEquals(n)).getValue());
            assertFalse(((BooleanValue)ts.less(n)).getValue());
            assertFalse(((BooleanValue)ts.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)ts.greater(n)).getValue());
            assertFalse(((BooleanValue)ts.greaterEqual(n)).getValue());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

        try {
            assertFalse(((BooleanValue)n.equals(n)).getValue());
            assertFalse(((BooleanValue)n.notEquals(n)).getValue());
            assertFalse(((BooleanValue)n.less(n)).getValue());
            assertFalse(((BooleanValue)n.lessEqual(n)).getValue());
            assertFalse(((BooleanValue)n.greater(n)).getValue());
            assertFalse(((BooleanValue)n.greaterEqual(n)).getValue());
            n.like(n);

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws IncompatibleTypesException DOCUMENT ME!
     */
    public void testStringValueEquals() throws IncompatibleTypesException {
        StringValue v1 = ValueFactory.createValue("hola");
        StringValue v2 = ValueFactory.createValue("hola");
        StringValue v3 = ValueFactory.createValue("holA");
        assertTrue(((BooleanValue) v1.equals(v2)).getValue());
        assertFalse(((BooleanValue) v1.equals(v3)).getValue());
        assertFalse(((BooleanValue) v2.equals(v3)).getValue());
    }

    /**
     * DOCUMENT ME!
     */
    public void testEscape() {
        assertTrue(ValueWriterImpl.escapeString("pp'pp").equals("pp''pp"));
        assertTrue(ValueWriterImpl.escapeString("pp''pp").equals("pp''''pp"));
    }

    public void testBooleanComparations() {
        Value vTrue = ValueFactory.createValue(true);
        Value vFalse = ValueFactory.createValue(false);
        try {
            assertTrue(!((BooleanValue)vTrue.greater(vTrue)).getValue());
            assertTrue(((BooleanValue)vTrue.greater(vFalse)).getValue());
            assertTrue(!((BooleanValue)vFalse.greater(vTrue)).getValue());
            assertTrue(!((BooleanValue)vFalse.greater(vFalse)).getValue());
            assertTrue(((BooleanValue)vFalse.greaterEqual(vFalse)).getValue());
            assertTrue(((BooleanValue)vTrue.greaterEqual(vTrue)).getValue());
            assertTrue(((BooleanValue)vTrue.greaterEqual(vFalse)).getValue());
            assertTrue(!((BooleanValue)vFalse.greaterEqual(vTrue)).getValue());
            assertTrue(!((BooleanValue)vTrue.less(vTrue)).getValue());
            assertTrue(!((BooleanValue)vTrue.less(vFalse)).getValue());
            assertTrue(((BooleanValue)vFalse.less(vTrue)).getValue());
            assertTrue(!((BooleanValue)vFalse.less(vFalse)).getValue());
            assertTrue(((BooleanValue)vTrue.lessEqual(vTrue)).getValue());
            assertTrue(!((BooleanValue)vTrue.lessEqual(vFalse)).getValue());
            assertTrue(((BooleanValue)vFalse.lessEqual(vTrue)).getValue());
            assertTrue(((BooleanValue)vFalse.lessEqual(vFalse)).getValue());
        } catch (IncompatibleTypesException e) {
            assertTrue(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCreateByType() throws Exception {
        assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
                Types.BIGINT).equals(ValueFactory.createValue(1L))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("true",
                Types.BIT).equals(ValueFactory.createValue(true))).getValue());
        assertTrue(((BooleanValue) ValueFactory.createValueByType("false",
                Types.BIT).equals(ValueFactory.createValue(false))).getValue());
        assertTrue(((BooleanValue) ValueFactory.createValueByType("true",
                Types.BOOLEAN).equals(ValueFactory.createValue(true))).getValue());
        assertTrue(((BooleanValue) ValueFactory.createValueByType("false",
                Types.BOOLEAN).equals(ValueFactory.createValue(false))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("carajo",
                Types.CHAR).equals(ValueFactory.createValue("carajo"))).getValue());
        assertTrue(((BooleanValue) ValueFactory.createValueByType("carajo",
                Types.VARCHAR).equals(ValueFactory.createValue("carajo"))).getValue());
        assertTrue(((BooleanValue) ValueFactory.createValueByType("carajo",
                Types.LONGVARCHAR).equals(ValueFactory.createValue("carajo"))).getValue());

        Calendar c = Calendar.getInstance();

        //month is 0-based
        c.set(1980, 8, 5, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date d = c.getTime();
        assertTrue(((BooleanValue) ValueFactory.createValueByType(
                DateFormat.getDateInstance().format(d), Types.DATE).equals(ValueFactory.createValue(
                    d))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("1.1",
                Types.DECIMAL).equals(ValueFactory.createValue(1.1d))).getValue());
        assertTrue(((BooleanValue) ValueFactory.createValueByType("1.1",
                Types.NUMERIC).equals(ValueFactory.createValue(1.1d))).getValue());
        assertTrue(((BooleanValue) ValueFactory.createValueByType("1.1",
                Types.DOUBLE).equals(ValueFactory.createValue(1.1d))).getValue());
        assertTrue(((BooleanValue) ValueFactory.createValueByType("1.1",
                Types.FLOAT).equals(ValueFactory.createValue(1.1d))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
                Types.INTEGER).equals(ValueFactory.createValue(1))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("1.1",
                Types.REAL).equals(ValueFactory.createValue(1.1f))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
                Types.SMALLINT).equals(ValueFactory.createValue(1))).getValue());

        assertTrue(((BooleanValue) ValueFactory.createValueByType("1",
                Types.TINYINT).equals(ValueFactory.createValue(1))).getValue());

        byte[] array = new byte[] { (byte) 255, (byte) 160, (byte) 7 };
        assertTrue(((BooleanValue) ValueFactory.createValueByType("FFA007",
                Types.BINARY).equals(ValueFactory.createValue(array))).getValue());

        c.set(1970, 0, 1, 22, 45, 20);
        c.set(Calendar.MILLISECOND, 0);

        Time t = new Time(c.getTime().getTime());
        assertTrue(((BooleanValue) ValueFactory.createValueByType(
                DateFormat.getTimeInstance().format(t), Types.TIME).equals(ValueFactory.createValue(
                    t))).getValue());

        c.set(1970, 0, 1, 22, 45, 20);
        c.set(Calendar.MILLISECOND, 2345);

        Timestamp ts = new Timestamp(c.getTime().getTime());
        assertTrue(((BooleanValue) ValueFactory.createValueByType(ts.toString(), Types.TIMESTAMP).equals(ValueFactory.createValue(
                    ts))).getValue());
    }

    public void testToStringFromStringCoherente() throws Exception {
        Value v = ValueFactory.createValue(13.5d);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.DOUBLE))).getValue());

        v = ValueFactory.createValue(13.5f);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.REAL))).getValue());

        v = ValueFactory.createValue(13L);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.BIGINT))).getValue());

        v = ValueFactory.createValue(true);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.BIT))).getValue());
        v = ValueFactory.createValue(false);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.BIT))).getValue());

        v = ValueFactory.createValue("hola");
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.VARCHAR))).getValue());

        Calendar c = Calendar.getInstance();

        //month is 0-based
        c.set(1980, 8, 5, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date d = c.getTime();
        v = ValueFactory.createValue(d);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.DATE))).getValue());

        v = ValueFactory.createValue(15);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.INTEGER))).getValue());

        v = ValueFactory.createValue((short) 13);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.SMALLINT))).getValue());

        v = ValueFactory.createValue((byte) 5);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.TINYINT))).getValue());

        v = ValueFactory.createValue(new byte[]{4,5,7,8,3,8});
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.BINARY))).getValue());

        c.set(1970, 0, 1, 22, 45, 20);
        c.set(Calendar.MILLISECOND, 0);

        Time t = new Time(c.getTime().getTime());
        v = ValueFactory.createValue(t);
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.TIME))).getValue());

        v = ValueFactory.createValue(new Timestamp(2465));
        assertTrue(((BooleanValue) v.equals(ValueFactory.createValueByType(v.toString(), Types.TIMESTAMP))).getValue());
    }
}
