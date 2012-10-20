package com.hardcode.gdbms.engine.data.persistence;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.parser.ParseException;

/**
 * ContentHandler that receives SAXEvents and generates a DataSource
 * 
 * @author Fernando González Cortés
 */
public class MementoContentHandler implements ContentHandler {
	private Stack mementos = new Stack();
	private Memento root;

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String name) throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator) {
	}

	/**
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
	 *      java.lang.String)
	 */
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
	 *      java.lang.String)
	 */
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (("operation".equals(localName)) || ("table".equals(localName))) {
			root = (Memento) mementos.pop();
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if ("operation".equals(localName)) {
			OperationLayerMemento memento = new OperationLayerMemento(
					atts.getValue("dataSourceName"), atts.getValue("sql"));

			if (mementos.size() > 0) {
				Memento m = (Memento) mementos.peek();

				if (m instanceof OperationLayerMemento) {
					OperationLayerMemento mem = (OperationLayerMemento) m;
					mem.addMemento(memento);
				}
			}

			mementos.push(memento);
		} else if ("table".equals(localName)) {
			DataSourceLayerMemento memento = new DataSourceLayerMemento(
					atts.getValue("table-name"), atts.getValue("table-alias"));
			if (mementos.size() > 0) {
				Memento m = (Memento) mementos.peek();

				if (m instanceof OperationLayerMemento) {
					OperationLayerMemento mem = (OperationLayerMemento) m;
					mem.addMemento(memento);
				}
			}

			mementos.push(memento);
		}
	}

	/**
	 * Get's the root memento of the XML parsed. Null if no parse has been done
	 * 
	 * @return The memento
	 */
	public Memento getRoot() {
		return root;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param m
	 *            DOCUMENT ME!
	 * @param dsf
	 *            DOCUMENT ME!
	 * @param mode
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * @throws ParseException
	 *             DOCUMENT ME!
	 * @throws SemanticException
	 *             DOCUMENT ME!
	 * @throws DriverLoadException
	 *             DOCUMENT ME!
	 * @throws NoSuchTableException
	 *             DOCUMENT ME!
	 * @throws ReadDriverException
	 *             TODO
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	private DataSource createDataSource(Memento m, DataSourceFactory dsf,
			int mode) throws ParseException, SemanticException,
			DriverLoadException, NoSuchTableException, EvaluationException,
			ReadDriverException {
		if (m instanceof OperationLayerMemento) {
			OperationLayerMemento olm = (OperationLayerMemento) m;

			for (int i = 0; i < olm.getMementoCount(); i++) {
				DataSource ds = createDataSource(olm.getMemento(i), dsf, mode);

			}
			DataSource ds = dsf.executeSQL(olm.getSql(), mode);
			dsf.changeDataSourceName(ds.getName(), olm.getName());
			return ds;
		} else if (m instanceof DataSourceLayerMemento) {
			DataSourceLayerMemento dslm = (DataSourceLayerMemento) m;

			return dsf.createRandomDataSource(dslm.getTableName(),
					dslm.getTableAlias(), mode);
		}

		throw new RuntimeException("unrecognized data source type");
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param dsf
	 *            DOCUMENT ME!
	 * @param mode
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * @throws DriverLoadException
	 *             DOCUMENT ME!
	 * @throws ParseException
	 *             DOCUMENT ME!
	 * @throws SemanticException
	 *             DOCUMENT ME!
	 * @throws NoSuchTableException
	 *             DOCUMENT ME!
	 * @throws EvaluationException
	 *             If there's any problem during expresion evaluation
	 * @throws ReadDriverException
	 *             TODO
	 */
	public DataSource getDataSource(DataSourceFactory dsf, int mode)
			throws DriverLoadException, ParseException, SemanticException,
			NoSuchTableException, EvaluationException, ReadDriverException {
		return createDataSource(root, dsf, mode);
	}
}
