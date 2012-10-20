package org.gvsig.remoteClient.sld.symbolizers;

import java.util.ArrayList;

import org.gvsig.remoteClient.sld.AbstractSLDSymbolizer;

import com.iver.cit.gvsig.fmap.core.FShape;

/**
 * Implements a symbolizer which can contain more than one SLDLineSymbolizer at
 * the same time
 * 
 * @see SLDLineSymbolizer
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDMultiLineSymbolizer extends AbstractSLDSymbolizer
		implements ISLDSymbolizer {

	protected ArrayList<SLDLineSymbolizer> lines = new ArrayList<SLDLineSymbolizer>();

	public abstract String toXML();

	public void addSldLine(SLDLineSymbolizer line) {
		this.lines.add(line);
	}

	public int getShapeType() {
		return FShape.LINE;
	}
}
