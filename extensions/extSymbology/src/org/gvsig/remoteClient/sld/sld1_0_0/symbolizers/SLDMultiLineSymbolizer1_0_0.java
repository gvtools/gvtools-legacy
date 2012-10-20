package org.gvsig.remoteClient.sld.sld1_0_0.symbolizers;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.symbolizers.SLDMultiLineSymbolizer;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;

/**
 * Implements a symbolizer which can contain more than one
 * SLDLineSymbolizer1_0_0 at the same time
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */
public class SLDMultiLineSymbolizer1_0_0 extends SLDMultiLineSymbolizer {

	public void parse(XMLSchemaParser parser) throws IOException,
			XmlPullParserException, LegendDriverException {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");

	}

	@Override
	public String toXML() {

		XmlBuilder xmlBuilder = new XmlBuilder();
		for (int i = 0; i < lines.size(); i++) {
			xmlBuilder.writeRaw(lines.get(i).toXML());
		}
		return xmlBuilder.getXML();
	}

}
