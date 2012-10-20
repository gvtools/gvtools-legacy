package org.gvsig.remoteClient.sld.sld1_0_0.symbolizers;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.symbolizers.SLDMultiPolygonSymbolizer;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;

/**
 * Implements a symbolizer which can contain more than one
 * SLDPolygonSymbolizer1_0_0 at the same time
 * 
 * @see SLDPolygonSymbolizer1_0_0
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */
public class SLDMultiPolygonSymbolizer1_0_0 extends SLDMultiPolygonSymbolizer {

	public String toXML() {
		XmlBuilder xmlBuilder = new XmlBuilder();
		for (int i = 0; i < polygons.size(); i++) {
			xmlBuilder.writeRaw(polygons.get(i).toXML());
		}
		return xmlBuilder.getXML();
	}

	public void parse(XMLSchemaParser parser) throws IOException,
			XmlPullParserException, LegendDriverException {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");

	}

}
