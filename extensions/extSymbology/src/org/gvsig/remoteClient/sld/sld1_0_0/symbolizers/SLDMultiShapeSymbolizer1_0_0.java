package org.gvsig.remoteClient.sld.sld1_0_0.symbolizers;

import org.gvsig.remoteClient.sld.symbolizers.SLDMultiShapeSymbolizer;

import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;

/**
 * Implements a symbolizer which can contain a SLDLineSymbolizer1_0_0, an
 * SLDPointSymbolizer1_0_0 and an SLDPolygonSymbolizer1_0_0 at the same time
 * 
 * @see SLDLineSymbolizer1_0_0
 * @see SLDPointSymbolizer1_0_0
 * @see SLDPolygonSymbolizer1_0_0
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */
public class SLDMultiShapeSymbolizer1_0_0 extends SLDMultiShapeSymbolizer {

	@Override
	public String toXML() {
		XmlBuilder xmlBuilder = new XmlBuilder();
		xmlBuilder.writeRaw(this.line.toXML());
		xmlBuilder.writeRaw(this.polygon.toXML());
		xmlBuilder.writeRaw(this.point.toXML());
		return xmlBuilder.getXML();
	}

}
