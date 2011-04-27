package org.gvsig.remoteClient.sld.symbolizers;

import java.util.ArrayList;

import org.gvsig.remoteClient.sld.AbstractSLDSymbolizer;

import com.iver.cit.gvsig.fmap.core.FShape;
/**
 * Implements a symbolizer which can contain more than one 
 * SLDPolygonSymbolizer at the same time
 * 
 * @see SLDPolygonSymbolizer
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDMultiPolygonSymbolizer extends AbstractSLDSymbolizer implements ISLDSymbolizer {

	protected ArrayList<SLDPolygonSymbolizer> polygons =  new ArrayList<SLDPolygonSymbolizer>();

	public abstract String toXML() ;
	
	public void addSldPolygon(SLDPolygonSymbolizer polygon) {
		this.polygons.add(polygon);
	}
	
	public int getShapeType() {
		return FShape.POLYGON;
	}	
	
	
}
