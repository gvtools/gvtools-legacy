package org.gvsig.remoteClient.sld.symbolizers;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.AbstractSLDSymbolizer;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements a symbolizer which can contain a SLDLineSymbolizer,
 * an SLDPointSymbolizer and an SLDPolygonSymbolizer at the same time
 * 
 * @see SLDLineSymbolizer
 * @see SLDPointSymbolizer
 * @see SLDPolygonSymbolizer
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDMultiShapeSymbolizer extends AbstractSLDSymbolizer implements ISLDSymbolizer{

	protected SLDLineSymbolizer line;
	protected SLDPolygonSymbolizer polygon;
	protected SLDPointSymbolizer point;
	
	
	public void addSldLine(SLDLineSymbolizer line) {
		this.line = line;
	}
	
	public void addSldPolygon(SLDPolygonSymbolizer polygon) {
		this.polygon = polygon;
	}
	public void addSldPoint(SLDPointSymbolizer point) {
		this.point = point;
	}
	
	public int getShapeType() {
		return FShape.MULTI;
	}

	public void parse(XMLSchemaParser parser) throws IOException, XmlPullParserException, LegendDriverException {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented");
		
	}

	public abstract String toXML();

}
