package com.iver.cit.gvsig.fmap.layers.layerOperations;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.iver.cit.gvsig.fmap.layers.FLayer;

public interface XMLItem {
	public void parse(ContentHandler handler) throws SAXException;

	public FLayer getLayer();
}
