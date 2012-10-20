package com.iver.cit.gvsig.fmap.layers;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.iver.utiles.XMLEntity;

/**
 * Genera eventos SAX relacionados con un XMLEntity
 * 
 * @author Fernando González Cortés
 */
public class GDBMSParser {
	XMLEntity xml;
	private ContentHandler handler;

	public GDBMSParser(XMLEntity xml) {
		this.xml = xml;
	}

	public void setContentHandler(ContentHandler handler) {
		this.handler = handler;
	}

	public void parse() throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		for (int i = 0; i < xml.getPropertyCount(); i++) {
			String key = xml.getPropertyName(i);
			String value = xml.getPropertyValue(i);

			atts.addAttribute("", key, key, "string", value);
		}

		handler.startElement("", xml.getName(), xml.getName(), atts);

		for (int i = 0; i < xml.getChildrenCount(); i++) {
			GDBMSParser p = new GDBMSParser(xml.getChild(i));
			p.setContentHandler(handler);
			p.parse();
		}

		handler.endElement("", xml.getName(), xml.getName());
	}
}
