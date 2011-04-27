package com.hardcode.gdbms.engine.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.persistence.Handler;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoContentHandler;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.hardcode.gdbms.parser.ParseException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class Tests extends DataSourceTestCase {
    /**
     * Tests the virtual primary key of files and objects
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFileAndObjectPK() throws Exception {
        DataSource d = ds.createRandomDataSource("persona",
                DataSourceFactory.MANUAL_OPENING);
        d.start();
        assertTrue(d.getFieldCount() == 7);
        assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(ValueFactory.createValue(
                    1))).getValue());
        assertTrue(d.getFieldName(0).equals("id"));
        assertTrue(d.getFieldName(1).equals("nombre"));
        assertTrue(d.getFieldName(2).equals("apellido"));
        assertTrue(d.getFieldName(3).equals("fecha"));
        assertTrue(d.getFieldName(4).equals("tiempo"));
        assertTrue(d.getFieldName(5).equals("marcatiempo"));
        assertTrue(d.getFieldName(6).equals("PK"));
        d.stop();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception
     * @throws RuntimeException DOCUMENT ME!
     */
    public void testDelegation() throws Exception {
        ds.setDelegating(false);

        DataSource d;

        try {
            d = ds.executeSQL("select apellido from hsqldbpersona;",
                    DataSourceFactory.MANUAL_OPENING);

            d.start();

            String aux = d.getAsString();
            d.stop();
            ds.setDelegating(true);

            d = ds.executeSQL("select apellido from hsqldbpersona;",
                    DataSourceFactory.MANUAL_OPENING);
            d.start();
            assertTrue(aux.equals(d.getAsString()));
            d.stop();
        } catch (DriverLoadException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (ReadDriverException e) {
            throw new RuntimeException(e);
        } catch (SemanticException e) {
            throw new RuntimeException(e);
        } finally {
            ds.clearViews();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws DriverException
     * @throws RuntimeException DOCUMENT ME!
     */
    public void testViewRemoving() throws ReadDriverException {
        ds.setDelegating(true);

        DataSource d1;
        DataSource d2;

        try {
            d1 = ds.executeSQL("select apellido from hsqldbpersona;",
                    DataSourceFactory.MANUAL_OPENING);
            d2 = ds.createRandomDataSource("hsqldbpersona",
                    DataSourceFactory.MANUAL_OPENING);
        } catch (DriverLoadException e1) {
            throw new RuntimeException(e1);
        } catch (ParseException e1) {
            throw new RuntimeException(e1);
        } catch (SemanticException e1) {
            throw new RuntimeException(e1);
        } catch (NoSuchTableException e) {
            throw new RuntimeException(e);
        } catch (EvaluationException e) {
            throw new RuntimeException(e);
        } finally {
            ds.clearViews();
        }

        try {
            d1.start();
            d1.stop();
            assertTrue("Views not deleted", false);
        } catch (ReadDriverException e) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws DriverException
     * @throws RuntimeException DOCUMENT ME!
     */
    public void testQueryDataSources() throws ReadDriverException {
        DataSource d1;

        try {
            d1 = ds.createRandomDataSource("hsqldbapellido",
                    DataSourceFactory.MANUAL_OPENING);
            ds.setDelegating(true);

            DataSource d2 = ds.executeSQL("select apellido from hsqldbpersona;",
                    DataSourceFactory.MANUAL_OPENING);

            d1.start();
            d2.start();
            assertTrue(d1.getAsString().equals(d2.getAsString()));
            d1.stop();
            d2.stop();
        } catch (DriverLoadException e) {
            throw new RuntimeException(e);
        } catch (NoSuchTableException e) {
            throw new RuntimeException(e);
        } catch (ReadDriverException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (SemanticException e) {
            throw new RuntimeException(e);
        } catch (EvaluationException e) {
            throw new RuntimeException(e);
        } finally {
            ds.clearViews();
        }
    }

    /**
     * Tests the DataSource.remove method
     *
     * @throws RuntimeException DOCUMENT ME!
     */
    public void testRemoveDataSources() {
        DataSource d = null;

        try {
            d = ds.createRandomDataSource("persona",
                    DataSourceFactory.MANUAL_OPENING);
            d.remove();
        } catch (DriverLoadException e) {
            throw new RuntimeException(e);
        } catch (NoSuchTableException e) {
            throw new RuntimeException(e);
        } catch (ReadDriverException e) {
            throw new RuntimeException(e);
        } catch (WriteDriverException e) {
        	throw new RuntimeException(e);
		}

        try {
            d = ds.createRandomDataSource("persona",
                    DataSourceFactory.MANUAL_OPENING);
            assertTrue(false);
        } catch (DriverLoadException e1) {
            throw new RuntimeException(e1);
        } catch (NoSuchTableException e1) {
            assertTrue(true);
        } catch (ReadDriverException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests the DataSourceFactory.removeAllDataSources method
     */
    public void testRemoveAllDataSources() {
        ds.removeAllDataSources();
        assertTrue(ds.getDriverInfos().length == 0);
    }

    /**
     * Tests the naming of operation layer datasource
     *
     * @throws Throwable DOCUMENT ME!
     */
    public void testOperationDataSourceName() throws Throwable {
        DataSource d = ds.executeSQL("select * from persona;",
                DataSourceFactory.MANUAL_OPENING);
        assertTrue(ds.createRandomDataSource(d.getName(),
                DataSourceFactory.MANUAL_OPENING) != null);
    }

    /**
     * Tests the persistence
     *
     * @throws Throwable DOCUMENT ME!
     */
    public void testXMLMemento() throws Throwable {
        DataSource d = ds.executeSQL("select * from persona;",
                DataSourceFactory.MANUAL_OPENING);
        Memento m = d.getMemento();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Handler h = new Handler();
        PrintWriter pw = new PrintWriter(out);
        h.setOut(pw);
        m.setContentHandler(h);
        m.getXML();
        pw.close();

        XMLReader reader = XMLReaderFactory.createXMLReader(
                "org.apache.crimson.parser.XMLReaderImpl");
        MementoContentHandler mch = new MementoContentHandler();
        reader.setContentHandler(mch);
        reader.parse(new InputSource(
                new ByteArrayInputStream(out.toByteArray())));

        DataSource n = mch.getDataSource(ds,
                DataSourceFactory.MANUAL_OPENING);

        n.start();
        d.start();
        assertTrue("Fallo en la persistencia",
            d.getAsString().equals(n.getAsString()));
        n.stop();
        d.stop();
    }

    /**
     * Tests the persistence
     *
     * @throws Throwable DOCUMENT ME!
     */
    public void testXMLMementoOfQueryDataSource() throws Throwable {
        DataSource d = ds.createRandomDataSource("hsqldbapellido",
                DataSourceFactory.MANUAL_OPENING);
        Memento m = d.getMemento();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Handler h = new Handler();
        PrintWriter pw = new PrintWriter(out);
        h.setOut(pw);
        m.setContentHandler(h);
        m.getXML();
        pw.close();

        XMLReader reader = XMLReaderFactory.createXMLReader(
                "org.apache.crimson.parser.XMLReaderImpl");
        MementoContentHandler mch = new MementoContentHandler();
        reader.setContentHandler(mch);
        reader.parse(new InputSource(
                new ByteArrayInputStream(out.toByteArray())));

        DataSource n = mch.getDataSource(ds,
                DataSourceFactory.MANUAL_OPENING);

        n.start();
        d.start();
        assertTrue("Fallo en la persistencia",
            d.getAsString().equals(n.getAsString()));
        n.stop();
        d.stop();

        ds.clearViews();
    }

    public void testChangeDataSourceName() throws Throwable {
        ds.changeDataSourceName("persona", "nuevonombre");

        try{
            ds.createRandomDataSource("persona");
            assertTrue(false);
        }catch (NoSuchTableException e) {
            assertTrue(true);
        }
        try{
            DataSource d = ds.createRandomDataSource("nuevonombre");
            assertTrue(true);
            assertTrue(d.getName() == "nuevonombre");
        }catch (NoSuchTableException e) {
            assertTrue(false);
        }
    }
}
