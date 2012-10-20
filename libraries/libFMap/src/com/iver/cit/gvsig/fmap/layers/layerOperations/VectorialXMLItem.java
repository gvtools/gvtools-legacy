package com.iver.cit.gvsig.fmap.layers.layerOperations;

import java.util.BitSet;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class VectorialXMLItem implements XMLItem {

	private BitSet bitset;
	private FLayer layer;

	public VectorialXMLItem(BitSet bitset, FLayer layer) {
		this.bitset = bitset;
		this.layer = layer;
	}

	public FLayer getLayer() {
		return layer;
	}

	/**
	 * @see com.iver.cit.gvsig.gui.toolListeners.InfoListener.XMLItem#parse(org.xml.sax.ContentHandler)
	 */
	public void parse(ContentHandler handler) throws SAXException {
		AttributesImpl aii = new AttributesImpl();
		handler.startElement("", "", ((FLayer) layer).getName(), aii);
		try {

			SelectableDataSource ds = ((AlphanumericData) layer).getRecordset();
			ds.start();

			FieldDescription[] fields = ds.getFieldsDescription();

			for (int j = bitset.nextSetBit(0); j >= 0; j = bitset
					.nextSetBit(j + 1)) {
				AttributesImpl ai = new AttributesImpl();

				for (int k = 0; k < ds.getFieldCount(); k++) {
					System.out.println("fieldName:" + ds.getFieldName(k)
							+ " fieldAlias:" + fields[k].getFieldAlias());
					ai.addAttribute("", ds.getFieldAlias(k),
							ds.getFieldAlias(k), "xs:string",
							ds.getFieldValue(j, k).toString());
				}
				handler.startElement("", "", String.valueOf(j), ai);
				handler.endElement("", "", String.valueOf(j));
			}

			ds.stop();

		} catch (ReadDriverException e) {
			throw new SAXException(e);
		}
		handler.endElement("", "", ((FLayer) layer).getName());
	}
}
